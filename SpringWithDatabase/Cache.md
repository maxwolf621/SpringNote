[Implementing a Cache with Spring Boot](https://reflectoring.io/spring-boot-cache/)   
[Getting Started With Spring Data Redis](https://frontbackend.com/spring-boot/getting-started-with-spring-data-redis)   
[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)   
[Annotations](https://howtodoinjava.com/spring-boot2/spring-boot-cache-example/)        
[Annotations and Flow diagram](https://sunitc.dev/2020/08/27/springboot-implement-caffeine-cache/)

[CACHE PROVIDERS](https://www.cnblogs.com/ejiyuan/p/11014765.html)   

[TOC]


## Caches And Database

[Note Taking From](https://medium.com/fcamels-notes/%E7%94%A8-caffeine-%E5%92%8C-redis-%E7%AE%A1%E7%90%86-cache-%E6%A1%88%E4%BE%8B%E5%88%86%E6%9E%90-23e88291b289)

[Properties of Annotations](https://juejin.cn/post/6844903966615011335)   

### RELATIONSHIP

- server：web server 或 API server。
- local cache：server 的 in-memory cache。 (`Caffeine`)
- external cache：多台 servers 共用的 cache server (`Redis`)
- database：儲存原始資料的 database server (`MySQL`)

### SCENARIO

當重開Server後需要一些時間才能填回local cache，這段時間會增加 database 的負載   
**我們可以將`local cache`的資料寫入`external cache`，重開後從`external cache`讀回來以此增加效率。**   

- 為降低 external cache 的負載，可以在 local cache 發出請求時多過一層 `Caffeine` 的 async load，用來統合同一個 key 多筆查詢成一筆對 external cache (`Redis`) and database (`MySQL`) 的查詢。

- 若有作 partition，等於來自同時間全部 servers 收到同一個 key 的查詢，總共只會發一次查詢到 external cache/database。 
    - e.g. **假設一次重開十台機器，十台機器每秒收到 100 筆同樣的查詢，等於將 1000 筆對 database 的查詢降為對 external cache 的 1 筆查詢。**

## Annotations

[Good Reference 1](https://morosedog.gitlab.io/springboot-20190411-springboot24/)   
[Good Reference 2](https://juejin.cn/post/6882196005731696654)    

### Spring Application Annotation 

`@EnableCaching` : Apply the application for caching
### Method Annotation


![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7dee92b136f04551b3e0061c3c36c056~tplv-k3u1fbpfcp-watermark.awebp) 
- different `cacheNames` map to specific Cache Objects
- use `cacheNames` instead of `value` is recommended

#### `@Cacheable(value , cacheNames, key, keyGenerator, cacheManager, cacheResolver, condition, unless, sync)` 
1. Cache the returned value from Database if the value doesn't exist

```java
@Override
@Cacheable(cacheNames = "books", condition = "#id > 1", sync = true)
public Book getById(Long id) {
    return new Book(String.valueOf(id), "some book");
}
```
- parameter `id` : is default-Key name for `@Cacheable`'s attribute `Key`

#### `@CachePut(value, cacheNames, key, keyGenerator, cacheManager, cacheResolver, condition, unless)`
1. update the cache 
2. often use for updating data method

difference btw `@CachePut` and `@Cacheable`
- `@Cacheable` : If an item is found in the cache , Method code is not executed .
- `@CachePut` : **Always execute method code , And update the cache after the method is executed**.
- `unless = expression` : expression is true then don't cache
- `condition = expression` : expression is true then cache 

#### `@CacheEvict(value, cacheNames, key, keyGenerator, cacheManager, cacheResolver, condition, allEntries, beforeInvocation`
1. deleting the data from cache
2. often use for deleting data method
3. `condition` only


```java
@CacheEvict(
   value = "persons", 
   key = "#person.emailAddress")
public void deletePerson(Person person)
```
- By default `@CacheEvict`, runs after method invocation.

```java
@CacheEvict(
   value = "persons", 
   allEntries = true, 
   beforeInvocation = true)
public void importPersons()
```
- `allEntries` : `true` then caches will be deleted once method invocation
- `beforeInvocation` : `true` then caches will be deleted before method invocation  

#### `@Caching`

```java
public @interface Caching {
	Cacheable[] cacheable() default {};
	CachePut[] put() default {};
	CacheEvict[] evict() default {};
}
```
It contains `@Cacheable` , `@CachePut` and `@CacheEvict`

### Class Annotation

`@CacheConfig`
- it allows to share the cache names (`KeyGenerator`、`CacheManager` and `CacheResolver`)
- Priority of CacheConfig's `cacheNames` is lower than method cache annotations' `cacheNames` or `value`

### Cache Key with `KeyGenerator`

This is responsible for generating every key for each data item in the cache, which would be used to lookup the data item on retrieval.   

The default implementation here is the `SimpleKeyGenerator`   
The caching abstraction uses a `simpleKeyGenerator` based on the following algorithm     
1. If no params are given, return `SimpleKey.EMPTY`.
2. If only one param is given, return that instance.
3. If more the one param is given, return a `SimpleKey` containing all parameters.

```java

@Override
public Object generate(Object target, 
                       Method method, 
                       Object... params) {
    return generateKey(params);
}

/**
 * Default Key Generator Implementation 
 * Generate a key based on the specified parameters.
 */
public static Object generateKey(Object... params) {
    if (params.length == 0) {
        return SimpleKey.EMPTY;
    }
    if (params.length == 1) {
        Object param = params[0];
        if (param != null && !param.getClass().isArray()) {
            return param;
        }
    }
    return new SimpleKey(params);
}
```

When to use custom key generator ?
Default `SimpleKeyGenerator` implementation (**uses the method parameters provided to generate a key.)** This means that if we have two methods that use the same cache name and set of parameter types, then there's a high probability that it will result in a collision.

For example :: Two Different methods with same method parameters
```java
@Override
@Cacheable(cacheNames = "books", sync = true)
public Book getByIsbn(String isbn) {
    simulateSlowService();
    return new Book(isbn, "Some book");
}

@Override
@Cacheable(cacheNames = "books", sync = true)
public String test(String test) {
    return test;
}


logger.info("test getByIsbn -->" + bookRepository.getByIsbn("test")); // key : test , return Book object
logger.info("test test -->" + bookRepository.test("test")); // key : test , but return String
```


Both methods are looking for key `test` and returning the different object type, it causes `ClassCastException`
```vim
Caused by: java.lang.ClassCastException: class com.example.caching.Book cannot be cast to class java.lang.String (com.example.caching.Book is in unnamed module of loader 'app'; java.lang.String is in module java.base of loader 'bootstrap')
	at com.sun.proxy.$Proxy33.test(Unknown Source) ~[na:na]
	at com.example.caching.AppRunner.run(AppRunner.java:23) ~[main/:na]
	at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:795) ~[spring-boot-2.3.2.RELEASE.jar:2.3.2.RELEASE]
	... 5 common frames omitted
```

To handle the exception we must define custom implementation of `KeyGenerator`
```java
@Component
public class MyKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getName() + method.getName() + 
                Stream.of(params).map(Object::toString).collect(Collectors.joining(","));
    }
}
```

```java
/**
  * <p> Add keyGenerator attribute to specify Cache Key </p>
  */
@Override
@Cacheable(cacheNames = "books", sync = true, keyGenerator = "myKeyGenerator")
public Book getByIsbn(String isbn) {
    simulateSlowService();
    return new Book(isbn, "Some book");
}

@Override
@Cacheable(cacheNames = "books", sync = true, keyGenerator = "myKeyGenerator")
public String test(String test) {
    return test;
}
```

## CacheManager

CacheManager configures Cache Providers to be used in Spring Boot (e.g `Caffeine`, `Redis` , ... etc) 

## Cache Configuration via `CachingConfigurerSupport`

[](https://www.javadevjournal.com/spring-boot/3-ways-to-configure-multiple-cache-managers-in-spring-boot/)  

Override these methods to configure our custom cache configuration
1. `CacheManager cacheManager()` : Return the cache manager bean to use for annotation-driven cache management.
2. `CacheResolver cacheResolver()` : Return the CacheResolver bean to use to resolve regular caches for annotation-driven cache management.
3. `CacheErrorHandler errorHandler()` : Return the CacheErrorHandler to use to handle cache-related errors.
4. `KeyGenerator keyGenerator()` : Return the key generator bean to use for annotation-driven cache management.


### [Create Custom `CacheErrorHandler`](https://hellokoding.com/spring-caching-custom-error-handler/)

To customize `ErrorHandling` 

```java
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.error(e.getMessage(), e);
    }
}
```
### Register `CacheErrorHandler` in `CachingConfigurerSupport`

```java
import xxx.yyy.CustomCacheErrorHandler;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;   
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfiguration extends CachingConfigurerSupport {  
    
    //...

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    //...
}
```

### CacheResolver 

Use it when 
1. Pick the cache manager on **case by case.**
2. Pick the cache manager **at runtime based on type of request**.


Need to override `public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context)`
```java
public class MultipleCacheResolver implements CacheResolver {
    
    // Managers
    private final CacheManager simpleCacheManager;
    private final CacheManager caffeineCacheManager;

    // CACHE NAMES
    private static final String ORDER_CACHE = "orders";    
    private static final String ORDER_PRICE_CACHE = "orderPrice";
    
    // (SETTER) Assign Managers 
    public MultipleCacheResolver(CacheManager simpleCacheManager,CacheManager caffeineCacheManager) {
        this.simpleCacheManager = simpleCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
        
    }

    /**
      * Specify Each Cache Name that responses to certain Cache Provides
      * @return the cache(s) to use for the specified invocation. 
      * (execution of a program or methods)
      */
    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        Collection<Cache> caches = new ArrayList<Cache>();
        
        if ("getOrderDetail".equals(context.getMethod().getName())) {

            caches.add(caffeineCacheManager.getCache(ORDER_CACHE));
        } else {

            caches.add(simpleCacheManager.getCache(ORDER_PRICE_CACHE));
        }
        return caches;
    }
}
```



Add Bean of `CacheResolver` in implementations of `CachingConfigurerSupport` then we can apply multiple caches managers for our application
```java
@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {


    // Managers
    @Bean
    @Override
    public CacheManager cacheManager() {

        // CaffeineCacheManager(String... cacheNames)
        // Construct a static CaffeineCacheManager, managing caches for the specified cache customer and orders
        // e.g. @Cacheable(cacheNames = customers , ...) or @Cacheable(cacheNames = orders , ...)
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("customers", "orders");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                    .initialCapacity(200)
                    .maximumSize(500)
                    .weakKeys()
                    .recordStats());
        
        return cacheManager;
    }

    @Bean
    @Override
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("customerOrders", "orderPrice");
    }

    // Bean of Resolver
    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(alternateCacheManager(), cacheManager());
    }
}
```

Add `cacheResolver` property for cache annotation
```java
@Component
public class OrderDetailBO {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Cacheable(cacheNames = "orders", cacheResolver = "cacheResolver")
    public Order getOrderDetail(Integer orderId) {
        return orderDetailRepository.getOrderDetail(orderId);
    }

    @Cacheable(cacheNames = "orderPrice", cacheResolver = "cacheResolver")
    public double getOrderPrice(Integer orderId) {
        return orderDetailRepository.getOrderPrice(orderId);
    }
}
```