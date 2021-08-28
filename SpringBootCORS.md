# CORS

[How to configure CORS in spring security](https://www.tpisoftware.com/tpu/articleDetails/1415)  
[Cors SetUp Baeldung](https://www.baeldung.com/spring-cors)  


[Waht is CORS](https://shubo.io/what-is-cors/#%E5%90%8C%E6%BA%90%E6%94%BF%E7%AD%96-same-origin-policy)    
[Spring Specifications](https://spring.io/guides/gs/rest-service-cors/)  

## CORS in Spring Boot

### CorsFilter 

[Other example](https://stackoverflow.com/questions/50184663/global-cors-configuration-breaks-when-migrating-to-spring-boot-2-0-x)  

```java
@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // allow origin 
        config.addAllowedOrigin("/*");

        // allow origin carry cookies (information)
        config.setAllowCredentials(true);

        //允許使用那些請求方式
        config.addAllowedMethod("/*");
        //config.setAllowedMethods(Arrays.asList("GET", "PUT", "POST","DELETE"));
        //config.addAllowedMethod(HttpMethod.POST);

        //允許哪些Header
        config.addAllowedHeader("/*");
        //config.addAllowedHeader("x-firebase-auth");

        //可獲取哪些Header（因為跨網域預設不能取得全部Header資訊）
        config.addExposedHeader("/*");
        //config.addExposedHeader("Content-Type");
        //config.addExposedHeader( "X-Requested-With");
        //config.addExposedHeader("accept");
        //config.addExposedHeader("Origin");
        //config.addExposedHeader( "Access-Control-Request-Method");
        //config.addExposedHeader("Access-Control-Request-Headers");


        //映射路徑
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //return一個的CorsFilter.
        return new CorsFilter(configSource);
    }

}
```

### Implementations of `WebMvcConfigurer`  

[Example from StackOverflow](https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application)

```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:4200")
                //.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedMethods("*")
                .maxAge(3600L)   
                .allowedHeaders("*")
                // allow header : Authorization
                .exposedHeaders("Authorization")
                //allowcookie
                .allowCredentials(true);
    }
}


@EnableWebSecurity
	public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http.cors().and()...
	    }
	}
```

### Annotation `@CrossOrigin`

With `@Controller` and `@RequestMapping(...)`

```java
/**
  * allow all origins access {@code remove} 
  * allow {@code http://example.com} to access {@code retrieve}
  * Both methods will have a maxAge of 3,600 second
  */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

    @CrossOrigin("http://example.com")
    @RequestMapping(method = RequestMethod.GET, "/{id}")
    public Account retrieve(@PathVariable Long id) {
        // ...
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    public void remove(@PathVariable Long id) {
        // ...
    }
}
```

### Response header

```java
@Controller
public class CorsController {

    @RequestMapping("/hello")
    @ResponseBody
    public String index(HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        return "Hello World";
    }

}
```
