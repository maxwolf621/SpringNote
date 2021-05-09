###### tags: `Spring Boot`
# Initialize Application of Spring Boot
[TOC]
A Spring Boot offer you build up a spring framework easily
It will do the followings automatically
1. put beans into Spring Container
2. configure the jar package or spring's core
3. scan components class, repository class, service class, controller of this application project

A Spring Application looks like
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```
- The `@SpringBootApplication` Annotation contains
    > `@SpringBootConfiguration`, `@EnableAutoConfiguration`, `@ComponentScan`

1. `@SpringBootConfiguration` - 繼承自 `@Configuration`, 標註當前類別是配置類, 並會將當前類別標記為@Bean的實例加入到spring Container
    > Put Beans into Spring Container
    >> A `@Configuration` class contains `@Beans` methods
3. `@EnableAutoConfiguration` - 啟動自動加入配置, 導入你所需要的jar包, 例如本專案有用到 spring-boot-starter-web, 而這是spring的core, 裡面包括了webmvc, tomcat....etc,也會被自動加入配置
    > Import jar package into Spring Core
5.`@ComponentScan` 掃描當前包與底下所有`@Controller`, `@Service`, `@Component`, `@Repository`
    > Scan this Application's `@Controller`, `@Service`, `@Component`, `@Repository`


That's why setUp for Spring Boot has much easier than Spring MVC  
