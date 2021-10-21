# Async in Spring

- [jianshu](https://www.jianshu.com/p/20a4e37314fc)
- [Spring Boot @Async 非同步方法範例    ](https://matthung0807.blogspot.com/2020/06/spring-boot-async-methods-example.html)


## Thread Configuration 

By default, executor uses `SimpleAsyncTaskExecutor`

To customize ThreadPoolExecutor 
```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "executor")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
```