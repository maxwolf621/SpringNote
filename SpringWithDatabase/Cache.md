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



