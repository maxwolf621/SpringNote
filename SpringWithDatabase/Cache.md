[Implementing a Cache with Spring Boot](https://reflectoring.io/spring-boot-cache/)
[A Guide To Caching in Spring](https://www.baeldung.com/spring-cache-tutorial)
[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)
[install wsl window 10 and redis](https://redis.com/blog/redis-on-windows-10/)
[disable wsl 10](https://www.windowscentral.com/install-windows-subsystem-linux-windows-10)


[redis](https://iter01.com/28296.html)


[(NO CACHE) Getting Started With Spring Data Redis](https://frontbackend.com/spring-boot/getting-started-with-spring-data-redis)
[](https://medium.com/@MatthewFTech/spring-boot-cache-with-redis-56026f7da83a)


[Spring Boot Cache with Redis](https://www.baeldung.com/spring-boot-redis-cache)



## Cache Configuration via `CachingConfigurerSupport`

### Register the CacheErrorHandler in `CachingConfigurerSupport`

It overrides `CacheErrorHandler`

```java
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

Add Bean of `CacheResolver` in `CachingConfigurerSupport` to apply multiple cache applications
```java
@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

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