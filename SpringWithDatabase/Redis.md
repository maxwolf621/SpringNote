# Redis

Redis Stands For Remote Dictionary Serve 

[Ref](https://www.mindbowser.com/spring-boot-with-redis-cache-using-annotation/)
[Ref2](https://www.netsurfingzone.com/spring-boot/spring-boot-redis-cache-example/)  
[Ref3](https://kumarshivam-66534.medium.com/implementation-of-spring-boot-data-redis-for-caching-in-my-application-218d02c31191)   


[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)
[install wsl window 10 and redis](https://redis.com/blog/redis-on-windows-10/)
[disable wsl 10](https://www.windowscentral.com/install-windows-subsystem-linux-windows-10)
## Jedis

To define connections settings from application client to Redis server, we need `Jedis` as API to help us

it's something similar to `JdbcTemplate` to connect with `MySql` Server.

## dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```


## application.properties

```vim
# **** REDIS (RedisProperties) ****
# **** spring.redis.XXXX       **** 

# Redis資料庫索引（預設為0）
spring.redis.database= 0

# Redis伺服器地址
spring.redis.host = localhost

# Redis伺服器連接端口
spring.redis.port = 6379

# Redis伺服器連接密碼（預設為空）
spring.redis.password =

# 連接池最大連接數（使用負值表示沒有限制）
spring.redis.pool.max-active = 8

# 連接池最大阻塞等待時間（使用負值表示沒有限制）
spring.redis.pool.max-wait = -1

# 連接池中的最大空閒連接
spring.redis.pool.max-idle = 8

# 連接池中的最小空閒連接
spring.redis.pool.min-idle = 0

# 連接超時時間（毫秒）
spring.redis.timeout = 1000
```

## Configuration 

Configure `RedisTemplate` objects
- `RedisTemplate` objects can be used for querying data (get data, delete data , ... etc)


### RedisTemplate's Serializer Types
They are implementation of `RedisSerializer<T>`    

- JDK  (DEFAULT)  (e.g.  key : `\xac\xed\x00\x05t\x00\x05KeyName`, value : `\xac\xed\x00\x05t\x00\x05Value` )
- String (MOST USED)
- JSON   
- XML     

[Other built-in Serializers](https://stackoverflow.com/questions/31608394/get-set-value-from-redis-using-redistemplate)


### Configuration Example 

```java
@Configuration
public class RedisConfig {
​
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // Create RedisTemplate Object
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        ​
        // Using Jackson2JsonRedisSerialize 
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
​
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
​
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
​
        redisTemplate.setConnectionFactory(connectionFactory);

        // Serializer for Key-Value Pair
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        // Serializer for Hash Key-Value Pair
        redisTemplate.setHashKeySerializer(new StringRedisSerializer()); 
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```

Configuration with `LettuceConnectionFactory`
```java
@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public LettuceConnectionFactory redis1LettuceConnectionFactory(RedisStandaloneConfiguration redis1RedisConfig,
                                                                   GenericObjectPoolConfig redis1PoolConfig) {
        LettuceClientConfiguration clientConfig =
                LettucePoolingClientConfiguration.builder()
                        .commandTimeout(Duration.ofMillis(100))
                        .poolConfig(redis1PoolConfig).build();

        return new LettuceConnectionFactory(redis1RedisConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redis1Template(
            @Qualifier("redis1LettuceConnectionFactory") LettuceConnectionFactory redis1LettuceConnectionFactory) {
        
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        // Key-Value
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash Key-Value
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // Enable Transaction Support 
        redisTemplate.setEnableTransactionSupport(true);
        
        // Connection Factory
        redisTemplate.setConnectionFactory(redis1LettuceConnectionFactory);
        
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Configuration
    public static class Redis1Config {
        @Value("${spring.redis1.host}")
        private String host;
        @Value("${spring.redis1.port}")
        private Integer port;
        @Value("${spring.redis1.password}")
        private String password;
        @Value("${spring.redis1.database}")
        private Integer database;

        @Value("${spring.redis1.lettuce.pool.max-active}")
        private Integer maxActive;
        @Value("${spring.redis1.lettuce.pool.max-idle}")
        private Integer maxIdle;
        @Value("${spring.redis1.lettuce.pool.max-wait}")
        private Long maxWait;
        @Value("${spring.redis1.lettuce.pool.min-idle}")
        private Integer minIdle;

        @Bean
        public GenericObjectPoolConfig redis1PoolConfig() {
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(maxActive);
            config.setMaxIdle(maxIdle);
            config.setMinIdle(minIdle);
            config.setMaxWaitMillis(maxWait);
            return config;
        }

        @Bean
        public RedisStandaloneConfiguration redis1RedisConfig() {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(host);
            config.setPassword(RedisPassword.of(password));
            config.setPort(port);
            config.setDatabase(database);
            return config;
        }
    }
}
```


[Configuration Example with `CachingConfigurerSupport`](https://www.tpisoftware.com/tpu/articleDetails/1525)   
```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {

		return new JedisConnectionFactory();
	}


    // custom keyGenerator
	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                
                // get class name
                sb.append(target.getClass().getName());
                // get method name
                sb.append(method.getName());
				
                // get other params
                for (Object obj : params) {
					sb.append(obj.toString());
				}
				
				return sb.toString();
			}
		};
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		
        redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

    // Assigning 
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {

		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
                
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(pair) // serializer 
				.entryTtl(Duration.ofHours(1)); //  expired time

		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
				.cacheDefaults(defaultCacheConfig).build();

	}

}
```

## Model

Each model must implement `Serializable`

```java
@Entity
public class Student implements Serializable{

    //...
}
```

## Tips

- Define `TTLs` : Time-to-live (TTL), is the time span after which your Cache will be deleting an entry. If you want to fetch data only once a minute, just guard it with a` @Cacheable` Annotation and set the TTL to `1` minute.

- Implement Serializable: If you are adding an object in Redis cache then the object should implement a Serializable interface.

- Redis Cache Limits: When cache size reaches the memory limit, old data is removed to make a place for a new one. Although Redis is very fast, it still has no limits on storing any amount of data on a 64-bit system. **It can only store 3GB of data on a 32-bit system.**

- Never Call Cacheable Method from the same class: The reason is that Spring proxy the access to these methods to make the Cache Abstraction work. When you call it within the same class this Proxy mechanic is not kicking in. By this, you basically bypass your Cache and make it non-effective.

- Use `Lettuce`, If you need something highly scalable: `Lettuce` is a scalable thread-safe, non-blocking Redis client based on netty and Reactor. `Jedis` is easy to use and supports a vast number of Redis features, however, it is not thread-safe and needs connection pooling to work in a multi-threaded environment.


## RedisTemplate methods for querying data 


for `String` : `opsForValue`
for `List` : `opsForList`
for `Set` :  `opsForSet`
for `Hash` : `opsForHash`
for `Zset` (sorted set) : `opsForZSet`

- [Example redisTemplate operations](https://zhuanlan.zhihu.com/p/139528556)
- [Example for custom operations](https://zhuanlan.zhihu.com/p/336033293)  
- [Example 2](https://blog.csdn.net/qq_36781505/article/details/86612988)

```java
@Autowired
RedisTemplate<String,Object> redisTemplate;
​
/**
  * redisTemplate for key
  */
String key = "example";

Boolean exist = redisTemplate.hasKey(key);

// set expired time for this key 
long time = 60;
// (key , time , unit)
redisTemplate.expire(key, time, TimeUnit.SECONDS);

// get expired time for this key
Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);

// delete via key
redisTemplate.delete(key);

/**
  * <p> optForHash </p>
  */

// save a hash key value pair
// via put(key, map_key, map_value) 
String key = "key_forCache";
String item = "map_key";
String value = "map_value";
redisTemplate.opsForHash().put(key, item, value);

// save a MAP
// via putAll(Key, Value)
@Autowired
RedisTemplate<String,Object> redisTemplate;

String key = "key_forCache";
Map<String, String> maps = new Map<String, String>();
maps.put("map_key_1", "map_value_1");
maps.put("map_key_2", "map_value_2");
redisTemplate.putAll(key, maps);

// get entries of a bucket via hash
String key = "bucket_name"
Map<String, String> entries = redisTemplate.opsForHash().entries(key);

// get map's value 
String key = "key_forCache";
String item = "map_key_1";
Object value = redisTemplate.opsForHash().get(key, item); // return value map_value_1

// delete item via key
redisTemplate.opsForHash().delete(key, item);

// Check if entry/item exists via key
String key = "map_forCache";

String item = "map_key_1";
Boolean exist = redisTemplate.opsForHash().hasKey(key, item);

/**
  * <p> Set </p>
  */

​// add values in the set whose name is cacheName
String key = "cacheName";
String value1 = "2";
String value2 = "1";
redisTemplate.opsForSet().add(key, value1, value2);


// get values of set
// [1,2]
Set<Object> members = redisTemplate.opsForSet().members(key);


// is value exists in set
String value = "2";
Boolean member = redisTemplate.opsForSet().isMember(key, value);
```