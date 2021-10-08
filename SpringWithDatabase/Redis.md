# Redis

[Ref](https://www.mindbowser.com/spring-boot-with-redis-cache-using-annotation/)
[Ref2](https://www.netsurfingzone.com/spring-boot/spring-boot-redis-cache-example/)  
Redis Stands For Remote Dictionary Serve 

[Ref3](https://kumarshivam-66534.medium.com/implementation-of-spring-boot-data-redis-for-caching-in-my-application-218d02c31191)   


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

```xml
# REDIS (RedisProperties)
# Redis資料庫索引（預設為0）
spring.redis.database=0
# Redis伺服器地址
spring.redis.host=localhost
# Redis伺服器連接端口
spring.redis.port=6379
# Redis伺服器連接密碼（預設為空）
spring.redis.password=
# 連接池最大連接數（使用負值表示沒有限制）
spring.redis.pool.max-active=8
# 連接池最大阻塞等待時間（使用負值表示沒有限制）
spring.redis.pool.max-wait=-1
# 連接池中的最大空閒連接
spring.redis.pool.max-idle=8
# 連接池中的最小空閒連接
spring.redis.pool.min-idle=0
# 連接超時時間（毫秒）
spring.redis.timeout=1000
```


## Configuration 

Configure `RedisTemplate`
- `RedisTemplate` can be used for querying data with a custom repository


RedisTemplate's Serializer Types

They are implementation of `RedisSerializer<T>`

- JDK   (e.g.  key : `\xac\xed\x00\x05t\x00\x05KeyName`, value : `\xac\xed\x00\x05t\x00\x05Value` )
- String (MOST USED)
- JSON   
- XML   


```java
@Configuration
public class RedisConfig {
​
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // Create RedisTemplate Object
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
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

```java
@Configuration
public class RedisConfig {

    @Bean
    @Primary
    public LettuceConnectionFactory redis1LettuceConnectionFactory(RedisStandaloneConfiguration redis1RedisConfig,GenericObjectPoolConfig redis1PoolConfig) {
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

`CachingConfigurerSupport` class and by overriding the `cacheManager()` method. 
This method returns a bean which will be the default cache manager for our application:

```java
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

	@Bean
	public JedisConnectionFactory redisConnectionFactory() {

		return new JedisConnectionFactory();
	}

	// key值命名
	@Bean
	public KeyGenerator wiselyKeyGenerator() {
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				StringBuilder sb = new StringBuilder();
               sb.append(target.getClass().getName());
				sb.append(method.getName());
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

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory factory) {

		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
                
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(pair) // 序列化方式
				.entryTtl(Duration.ofHours(1)); // 過期時間

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
Redis Cache Limits: When cache size reaches the memory limit, old data is removed to make a place for a new one. Although Redis is very fast, it still has no limits on storing any amount of data on a 64-bit system. It can only store 3GB of data on a 32-bit system.
Never Call Cacheable Method from the same class: The reason is that Spring proxy the access to these methods to make the Cache Abstraction work. When you call it within the same class this Proxy mechanic is not kicking in. By this, you basically bypass your Cache and make it non-effective.
Use Lettuce, If you need something highly scalable: Lettuce is a scalable thread-safe, non-blocking Redis client based on netty and Reactor. Jedis is easy to use and supports a vast number of Redis features, however, it is not thread-safe and needs connection pooling to work in a multi-threaded environment.

