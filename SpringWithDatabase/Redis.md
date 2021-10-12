# Redis

Redis Stands For Remote Dictionary Serve 

[Ref](https://www.mindbowser.com/spring-boot-with-redis-cache-using-annotation/)     
[Ref2](https://www.netsurfingzone.com/spring-boot/spring-boot-redis-cache-example/)     
[Ref3](https://kumarshivam-66534.medium.com/implementation-of-spring-boot-data-redis-for-caching-in-my-application-218d02c31191)       
[Ref4](https://medium.com/brucehsu-backend-dev/%E5%88%A9%E7%94%A8spring-cache%E5%84%AA%E9%9B%85%E7%9A%84%E4%BD%BF%E7%94%A8caches-5aad2630eb0a)  

[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)   
[Install wsl window 10 and redis](https://redis.com/blog/redis-on-windows-10/)   
[Disable wsl window 10](https://www.windowscentral.com/install-windows-subsystem-linux-windows-10)   
[MatthewFTech spring-boot-cache-with-redis](https://medium.com/@MatthewFTech/spring-boot-cache-with-redis-56026f7da83a)   


[Difference btw Lettuce and Jedis](https://github.com/spring-projects/spring-session/issues/789)  

[TOC]

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

#### `RedisCacheManager`  Defaults
![圖 2](../images/9bee8da418519b37952a044fad50265bfb38a241a4ce86331568832ed0d5daa3.png)  

#### `RedisCacheConfiguration` Defaults

![圖 3](../images/5a5e3f4b1e429ba8e17b952e92236bc2f1c29fa792ea2dcdf4ae3f2d57acf5e6.png)  


Spring Redis provides an implementation for the Spring cache abstraction through the `org.springframework.data.redis.cache` package

```java 
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
	return RedisCacheManager.create(connectionFactory);
}
```

To have Custom Configuration for Redis (e.g. Redis Cache and Redis Manager) we can do as the following 
```java
@Configuration
@EnableCaching
public class CacheConfig {

    /**
      * <p> To create RedisCacheManager as An Cache Provider </p>
      * <p> RedisCacheManager must build with Redis Connection Factory and 
      *     RedisCacheConfiguration </p>
      */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                                                             .entryTtl(Duration.ofMinutes(30));
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                                .cacheDefaults(redisCacheConfiguration)
                                .build();
    }
}
```

### Cache Manager Behavior for Redis

`RedisCacheManager` behavior can be configured with `RedisCacheManagerBuilder`
```java
RedisCacheManager cm = RedisCacheManager.builder(connectionFactory)
	.cacheDefaults(defaultCacheConfig())
	.withInitialCacheConfigurations(singletonMap("predefined", defaultCacheConfig().disableCachingNullValues()))
	.transactionAware()
	.build();
```


### Redis Cache behavior 

The behavior of `RedisCache` created with `RedisCacheManager` is defined with `RedisCacheConfiguration`. 
- The configuration lets you set `key` expiration `times`, `prefixes`, and `RedisSerializer` implementations for converting to and from the binary storage format

```java
var config = RedisCacheConfiguration.defaultCacheConfig()
                                    .entryTtl(Duration.ofSeconds(1))
	                                  .disableCachingNullValues();        
```

`RedisCacheManager` defaults to a lock-free `RedisCacheWriter` for reading and writing binary values. 
- **Lock-free caching improves throughput**. The lack of entry locking can lead to overlapping, non-atomic commands for the `putIfAbsent` and `clean` methods, as those require multiple commands to be sent to Redis. 
- the locking counterpart prevents command overlap by setting an explicit lock key and checking against presence of this key, which leads to additional requests and potential command wait times.

```java
RedisCacheManager cm = RedisCacheManager.build(RedisCacheWriter.lockingRedisCacheWriter())
	.cacheDefaults(defaultCacheConfig())
	...
```


### Prefix for `Key`

By default, **any key for a cache entry gets prefixed** with the actual cache name followed by two colons. This behavior can be changed to a `static` as well as a computed prefix.

```java
// static key prefix
RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith("( ͡° ᴥ ͡°)");

//The following example shows how to set a computed prefix:

// computed key prefix
RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheName -> "¯\_(ツ)_/¯" + cacheName);
```


## `RedisTemplate` Configuration

Configure `RedisTemplate` objects
- `RedisTemplate` objects can be used for querying data (get data, delete data , ... etc)


### RedisTemplate's Serializer Types
They are implementation of `RedisSerializer<T>`    

- JDK  (**DEFAULT**)  (e.g.  key : `\xac\xed\x00\x05t\x00\x05KeyName`, value : `\xac\xed\x00\x05t\x00\x05Value` )
- String (MOST USED)
- JSON   
- XML     

[Other built-in Serializer](https://stackoverflow.com/questions/31608394/get-set-value-from-redis-using-redistemplate)


### Configuration Examples

```java
@Configuration
public class RedisConfig {

    // ... factory ...
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
- [`ObjectMapper`](https://www.baeldung.com/jackson-object-mapper-tutorial)

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


##  Querying with Objects through `RedisTemplate`

RedisTemplate uses a Java-based serializer for most of its operations. 
- Any object written or read by the template is `serialized` and `deserialized` through Java

![圖 1](../images/4d977474d0350aed41916aba72c9ab7a94e16df3c7d5278667a438e9a8279cfc.png)  
- `String` : `opsForValue`
- `List` : `opsForList`
- `Set` :  `opsForSet`
- `Hash` : `opsForHash`
- `Sorted set` : `opsForZSet`

- [Example redisTemplate operations](https://zhuanlan.zhihu.com/p/139528556)
- [Example for custom operations](https://zhuanlan.zhihu.com/p/336033293)  
- [Example 2](https://blog.csdn.net/qq_36781505/article/details/86612988)


The Redis modules provides two extensions to `RedisConnection` and `RedisTemplate`, respectively the `StringRedisConnection` (and its `DefaultStringRedisConnection` implementation) and `StringRedisTemplate` as a convenient one-stop solution for intensive String operations. 

**In addition to being bound to String keys, the template and the connection use the `StringRedisSerializer` underneath, which means the stored keys and values are human-readable**



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
 


## Jedis Connector 

To define connections settings from application client to Redis server, we need `Jedis` as API to help us

it's something similar to `JdbcTemplate` to connect with `MySql` Server.

### Jedis Dependency

```xml
<dependencies>
  
  <dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.6.3</version>
  </dependency>

</dependencies>
```
### Configure Jedis Configuration

Create `JedisConnectionFactory` object for redis

```java
@Configuration
class AppConfig {

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {
    return new JedisConnectionFactory();
  }
}
```
### `JedisConnectionFactory` for Redis Configuration

```java
@Configuration
class RedisConfiguration {

  @Bean
  public JedisConnectionFactory redisConnectionFactory() {

    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("server", 6379);
    return new JedisConnectionFactory(config);
  }
}
```

- [Redis:Sentinel](https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:sentinel)



## Hash Mapping 

We can achieve a more sophisticated mapping of structured objects by using Redis hashes. 

Use Cases
- Direct mapping, by using `HashOperations` and a serialize
- Using Redis Repositories
- Using `HashMapper` and `HashOperations`

> Hash mappers are converters of map objects to a `Map<K, V>`

```java
public class Person {
  String firstname;
  String lastname;

  // …
}

public class HashMapping {

  // query the data (Person)
  @Autowired
  HashOperations<String, byte[], byte[]> hashOperations;

  // mapping the data (Person)
  HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();

  // save
  public void writeHash(String key, Person person) {

    Map<byte[], byte[]> mappedHash = mapper.toHash(person);

    hashOperations.putAll(key, mappedHash);
  }

  // get 
  public Person loadHash(String key) {

    Map<byte[], byte[]> loadedHash = hashOperations.entries("key");
    
    return (Person) mapper.fromHash(loadedHash);
  }
}
```


#### `Jackson2HashMapper`

`Jackson2HashMapper` can map top-level properties as Hash field names and, optionally, flatten the structure.

For example nested data structure
```java
public class Person {
  String firstname;
  String lastname;
  Address address;  // nested <------------------------
  Date date;
  LocalDateTime localDateTime;
}

public class Address {
  String city;
  String country;
}
```

```vim
address : { "city" : "Castle Black", "country" : "The North" }

flatten the structure

address.city : Castle Black  
address.country : The North  
```

## `@Transactional` Support

For use this annotations by enabling explicitly transaction support for each `RedisTemplate` by setting `setEnableTransactionSupport(true)`

Enabling transaction support binds RedisConnection to the current transaction backed by a ThreadLocal. 

- If the transaction finishes without errors, the Redis transaction gets committed with `EXEC`, otherwise rolled back with `DISCARD`. 

Redis transactions are batch-oriented. Commands issued during an ongoing transaction are queued and only applied when committing the transaction.

Spring Data Redis distinguishes between read-only and write commands in an ongoing transaction.  
- Read-only commands, such as `KEYS`, are **PIPED** to a fresh (non-thread-bound) `RedisConnection` to allow reads. 
- Write commands are **QUEUED** by `RedisTemplate` and applied upon commit.

