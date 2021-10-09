# Caffeine


There are two methods to use Caffeine 
1. Via Caffeine class methods
2. Via Spring Cache (e.g. `@Cacheable(...)`)

## Configuration for Caffeine in Spring Boot

```java
/**
  * To configure Cache in Spring Boot
  * <li> create xxxxCacheManager object </li>
  * <li> create xxxx cache object and configure it via Builder Pattern </li> 
  * Set xxxxCacheManager use this xxxx cache object
  */

@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    @Bean
    public CacheManager cacheManager(){
        // create manager
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // create caffeine object
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                                            .expireAfterWrite(10, TimeUnit.SECONDS)
                                            .initialCapacity(100)
                                            .maximumSize(1000);
                                            
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
```

#### Configurations of Caffeine

`initialCapacity=[integer]` : Initial Capacity of Cache
`maximumSize=[long]`: Maximum Size of Caches
`maximumWeight=[long]`: Maximum Weight of Caches
- `maximumSize` and `maximumWeight` cant be configured at the same time

`expireAfterAccess=[duration]`: duration for caches to be expired after your last access
`expireAfterWrite=[duration]`: duration for caches to be expired after Write
`refreshAfterWrite=[duration]`: interval of refresh the caches after Write
- Priority of `expireAfterWrite` is higher than `expireAfterAccess`
- If `expireAfterWrite` or `expireAfterAccess` is requested entries may be evicted on each cache modification, on occasional cache accesses, or on calls to `Cache.cleanUp()`. 
- Expired entries may be counted by `Cache.size()`, but will never be visible to read or write operations.

`recordStats`：Strategy mode 

## Weak and Soft Reference

To specify the data stored in Cache we have
```
weakKeys
softValues
weakValues
```

```java
Caffeine.newBuilder().softValues().build();
Caffeine.newBuilder().weakKeys().weakValues().build();
```

By default, the returned cache uses equality comparisons (the equals method) to determine equality for keys or values. 
- If `weakKeys()` was specified, the cache uses identity (`==`) comparisons instead for keys.    
- Likewise, if `weakValues()` or `softValues()` was specified, the cache uses identity(`==`)comparisons for values.

#### Cache Management for Soft and Weak

- If `weakKeys`, `weakValues`, or `softValues` are requested, it is possible for a key or value present in the cache to be **reclaimed by the garbage collector(GC)**.

- Entries with reclaimed keys or values may be removed from the cache on each cache modification, on occasional cache accesses, or on calls to `Cache.cleanUp()`
    > Entries may be counted in `Cache.size()`, but will never be visible to read or write operations.

- `weakValues` and `softValues` cant be configured at the same time
    
- use `weakValues()` when you want entries whose values are weakly reachable to be garbage collected (Delete the cache while GC scan it)

- `softValues()` is good for caching... if you have a `Map<Integer, Foo>` and **you want entries to to be removable in response to memory demand(Delete the caches if the memory capacity reaches limit)**, you'd want to use it.    

- You wouldn't want to use `weakKeys()` or `softKeys()` because they both use `==` identity, which would cause problems for you


#### Notes  

- Entries are automatically evicted from the cache when any of `maximumSize`, `maximumWeight`, `expireAfterWrite`, `expireAfterAccess`, `weakKeys`, `weakValues`, or `softValues` are requested.
    > If `maximumSize` or `maximumWeight` is requested entries may be evicted on each cache modification.

- The `Cache.cleanUp()` method of the returned cache will also perform maintenance, but calling it should not be necessary with a high throughput cache. 
Only caches built with `removalListener`, `expireAfterWrite`, `expireAfterAccess`, `weakKeys`, `weakValues`, or `softValues` perform periodic maintenance.

- The caches produced by `CacheBuilder` are serializable, and the deserialized caches retain all the configuration properties of the original cache. 

- The serialized form does not include cache contents, but only configuration.


## use Cache via Caffeine Method

```java
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {


    private UserRepository userRepo;

    // use Caffeine Methods 
    // with key : string  - value : Object pair
    @Autowired
    Cache<String, Object> caffeineCache;

    @Override
    public void addUserInfo(UserInfo userInfo) {

        userRepo.save(userInfo);

        // put data to cache
        // key : user_id , value userInfo : object
        caffeineCache.put(String.valueOf(userInfo.getId()),userInfo);
    }

    @Override
    public UserInfo getByName(Integer id) {

        // get from cache if present 
        caffeineCache.getIfPresent(id);
        UserInfo userInfo = (UserInfo) caffeineCache.asMap().get(String.valueOf(id));
        if (userInfo != null){
            return userInfo;
        }

        UserInfo userInfo = userRepo.findById(id)
                                    .map(user ->{
                                            caffeineCache.put(String.valueOf(userInfo.getId()),userInfo) })
                                    .orElseThrow(()-> new RuntimeException("Not Found"));
        )
        
        return userInfo;
    }

    @Override
    public UserInfo updateUserInfo(UserInfo updateUserInfo) {
        //...
        // update the data in cache
        
        caffeineCache.put(String.valueOf(updateUserInfo.getId()),updateUserInfo);
        
        return oldUserInfo;
    }

    @Override
    public void deleteById(Integer id) {
        
        userRepo.findById(id).ifPResentOrElse(
            () -> caffeineCache.asMap().remove(String.valueOf(id)),
            () -> new RuntimeException("Not Found");
        )
        userRepo.deleteById(id)    
    }

}
```

## Assistance via Spring Cache 

Add dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```java 
@Slf4j
@Service
@CacheConfig(cacheNames = "caffeineCacheManager")
public class UserInfoServiceImpl implements UserInfoService {

    // out database 
    private HashMap<Integer, UserInfo> userInfoMap = new HashMap<>();


    @Override
    @CachePut(key = "#userInfo.id")
    public void addUserInfo(UserInfo userInfo) {
        
        userInfoMap.put(userInfo.getId(), userInfo);
    }


    @Override
    @Cacheable(key = "#id")
    public UserInfo getByName(Integer id) {
        log.info("get");
        return userInfoMap.get(id);
    }

    @Override
    @CachePut(key = "#userInfo.id")
    public UserInfo updateUserInfo(UserInfo userInfo) {

        if (!userInfoMap.containsKey(userInfo.getId())) {
            return null;
        }

        UserInfo oldUserInfo = userInfoMap.get(userInfo.getId());

        if (!StringUtils.isEmpty(oldUserInfo.getAge())) {
            oldUserInfo.setAge(userInfo.getAge());
        }
        if (!StringUtils.isEmpty(oldUserInfo.getName())) {
            oldUserInfo.setName(userInfo.getName());
        }
        if (!StringUtils.isEmpty(oldUserInfo.getSex())) {
            oldUserInfo.setSex(userInfo.getSex());
        }

        userInfoMap.put(oldUserInfo.getId(), oldUserInfo);

        return oldUserInfo;
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(Integer id) {

        userInfoMap.remove(id);
    }

}
```


