[Implementing a Cache with Spring Boot](https://reflectoring.io/spring-boot-cache/)
[A Guide To Caching in Spring](https://www.baeldung.com/spring-cache-tutorial)


[Getting Started With Spring Data Redis](https://frontbackend.com/spring-boot/getting-started-with-spring-data-redis)
[](https://medium.com/@MatthewFTech/spring-boot-cache-with-redis-56026f7da83a)

[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)   
[Annotations](https://howtodoinjava.com/spring-boot2/spring-boot-cache-example/)   


## CacheManager

CacheManager configures Cache Providers (e.g `Caffeine`, `Reddis` , ... etc) 
## Cache Configuration via `CachingConfigurerSupport`

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


Create Resolver 
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
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("customerOrders", "orderprice");
    }

    // Bean of Resolver
    @Bean
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(alternateCacheManager(), cacheManager());
    }
}
```

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