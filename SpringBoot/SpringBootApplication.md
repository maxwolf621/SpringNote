###### tags: `Spring Boot`
# Initialize Application of Spring Boot

Good thing about a Spring Boot is it offers you build up a spring framework easily(quickly)    

Via annotation `@SpringBootApplication` it will do the followings automatically
1. **Put beans into Spring Container**
2. **Configure the jar package or spring's core**
3. **Scan components class, repository class, service class, controller of this application project**

A (Original) Spring(boot) Application looks like the following
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
     > 例如Application(our project)會用到`spring-boot-starter-web`的SpringBoot的核心架構(裡面包括了webmvc, tomcat....etc)利用該annotation就會被自動加入配置   
  3. `@ComponentScan` 掃描當前**PACKAGE包**與底下所有`@Controller`, `@Service`, `@Component`, `@Repository`
     > Scan this Application's `@Controller`, `@Service`, `@Component`, `@Repository`

We can also add up other (usual) annotations, for example...
- `@EnableConfigurationProperties({Properties.class})` : use custom class named `Properties.class` that operates configurations in file `application.properties` - -
  > or using `@ConfigurationPropertiesScan()`
- `@EnableAsync` :　Enables Spring's asynchronous method execution capability
- `@Import(SwaggerConfig.class)` : import our custom configuration (e.g `SwaggerConfig.class`)


# Spring Boot `RestController`
## Controller
```java
@RestController
@RequestMapping("/api")
public class MemberController {
    @Autowired
    private MemberRepository memberRepository;
    /**
     * when the user in the uri : `../api/members`
     */   
    @GetMapping("/members")
    public Collection<Member> members() {
        // it will display all members on the webpage via Repository
        return memberRepository.findAll();
    }
    
    //...
}

```
- We can have different controls for different _Repositories/Servers_ in the Controller
- `@RestController` = `@Controller` + `@ResponseBody`, 
    > **ResponseBody會將返回結果直接寫在Http Response Body中**, 因為我們資料傳輸時通常只傳回Json, 所以大部分都會使用這Annotation頁面的導頁與指定會交由前端(React,Angular...)來做
- **`@GetMapping`,` @PostMapping`,` @PutMapping`, `@DeleteMapping`與Http的GET, POST, PUT, DELETE等Methods相對應, 只要遵守RESTful規範直接使用即可**
