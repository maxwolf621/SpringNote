[Implementing a Cache with Spring Boot](https://reflectoring.io/spring-boot-cache/)
[A Guide To Caching in Spring](https://www.baeldung.com/spring-cache-tutorial)


[Getting Started With Spring Data Redis](https://frontbackend.com/spring-boot/getting-started-with-spring-data-redis)
[](https://medium.com/@MatthewFTech/spring-boot-cache-with-redis-56026f7da83a)

[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)   
[Annotations](https://howtodoinjava.com/spring-boot2/spring-boot-cache-example/)   

[](https://juejin.cn/post/6882196005731696654)


## Annotations

`EnableCaching` : Apply the application for caching


### Method Annotation

[Ref](https://juejin.cn/post/6882196005731696654)   

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7dee92b136f04551b3e0061c3c36c056~tplv-k3u1fbpfcp-watermark.awebp) 
- different `cacheNames` map to specific Cache Objects
- use `cacheNames` instead of `value` is recommended

`@Cacheable` 
1. Save the returned value from Database if the value doesn't exist
2. often use for querying data

```java
@Override
@Cacheable(cacheNames = "books", condition = "#id > 1", sync = true)
public Book getById(Long id) {
    return new Book(String.valueOf(id), "some book");
}
```

`@CachePut`
1. update the cache 
2. often use for updating data method

`@CacheEvict`
1. deleting the data from cache
2. often use for deleting data method

`@Caching`

```java
public @interface Caching {
	Cacheable[] cacheable() default {};
	CachePut[] put() default {};
	CacheEvict[] evict() default {};
}
```
It contains `@Cheable` , `@CachePut` and `@CacheEvict`

### Class Annotation

`@CacheConfig`


### cache's key problem

Cache Key Generator uses `SimpleKeyGenerator` by default.
```java
@Override
public Object generate(Object target, 
                       Method method, 
                       Object... params) {
    return generateKey(params);
}

/**
 * Default Key Generator
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

For example two key but returned different type of value
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

to handle the exception we must define implementation of `KeyGenerator`
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
  * add keyGenerator attribute to specify desirable Key 
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

CacheManager configures Cache Providers (e.g `Caffeine`, `Redis` , ... etc) 

## Cache Configuration via `CachingConfigurerSupport`

https://www.javadevjournal.com/spring-boot/3-ways-to-configure-multiple-cache-managers-in-spring-boot/

We can override these methods to configure our custom cache configuration
`CacheManager cacheManager()` : Return the cache manager bean to use for annotation-driven cache management.
`CacheResolver cacheResolver()` : Return the CacheResolver bean to use to resolve regular caches for annotation-driven cache management.
`CacheErrorHandler errorHandler()` : Return the CacheErrorHandler to use to handle cache-related errors.
`KeyGenerator keyGenerator()` : Return the key generator bean to use for annotation-driven cache management.


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
### Register the CacheErrorHandler in `CachingConfigurerSupport`

It overrides `CacheErrorHandler`

```java
import xxx.yyy.CustomCacheErrorHandler
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;   
import org.springframework.context.annotation.Configuration;

@Configuration
public class CachingConfiguration extends CachingConfigurerSupport {  
    
    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }
}
```

### CacheResolver 

Using `CacheResolver` 
- If you need to pick the cache manager on case by case.
- You need to pick the cache manager **at runtime based on type of request**.


Need to override `public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context)`
```java
public class MultipleCacheResolver implements CacheResolver {
    
    // Managers
    private final CacheManager simpleCacheManager;
    private final CacheManager caffeineCacheManager;

    // CACHE NAMES
    private static final String ORDER_CACHE = "orders";    
    private static final String ORDER_PRICE_CACHE = "orderprice";
    
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
        return new ConcurrentMapCacheManager("customerOrders", "orderprice");
    }

    // Bean of Resolver
    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(alternateCacheManager(), cacheManager());
    }
}
```

add `cacheResolver` property for cache annotation
```java
@Component
public class OrderDetailBO {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Cacheable(cacheNames = "orders", cacheResolver = "cacheResolver")
    public Order getOrderDetail(Integer orderId) {
        return orderDetailRepository.getOrderDetail(orderId);
    }

    @Cacheable(cacheNames = "orderprice", cacheResolver = "cacheResolver")
    public double getOrderPrice(Integer orderId) {
        return orderDetailRepository.getOrderPrice(orderId);
    }
}
```