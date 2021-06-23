###### tags: `Spring Boot`
# Initialize Application of Spring Boot
[TOC]  
Good thing about a Spring Boot is it offers you build up a spring framework easily(quickly)  

It will do the followings automatically (via annotation `@SpringBootApplication`)  
1. put beans into Spring Container
2. configure the jar package or spring's core
3. scan components class, repository class, service class, controller of this application project

A (Original) Spring Application looks like the following
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
- The `@SpringBootApplication` Annotation contains `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan`  
1. `@SpringBootConfiguration` - 繼承自 `@Configuration`, 標註當前類別是配置類, 並會將當前class標記為@Bean的instance加入(inject)到spring Container
    > A `@Configuration` class contains `@Beans` methods
2. `@EnableAutoConfiguration` - 啟動自動加入配置, 導入你所需要的jar包
    > 例如本專案有用到 spring-boot-starter-web, 而這是spring的core, 裡面包括了webmvc, tomcat....etc,也會被自動加入配置
3.`@ComponentScan` 掃描當前**PACKAGE包**與底下所有`@Controller`, `@Service`, `@Component`, `@Repository`
    > Scan this Application's `@Controller`, `@Service`, `@Component`, `@Repository`
