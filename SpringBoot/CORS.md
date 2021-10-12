###### tags: `Spring`

# Spring CORS

[What is CORS](https://shubo.io/what-is-cors/)  
[Spring CORS](https://spring.io/guides/gs/rest-service-cors/#controller-method-cors-configuration)  
[How to configure CORS in spring security](https://www.tpisoftware.com/tpu/articleDetails/1415)     
[Cors SetUp Baeldung](https://www.baeldung.com/spring-cors)          

There are 3 way to configure(disable) Spring boot CORS

##  controller-level CORS configuration `@CrossOrigin`
```java
@CrossOrigin(origins = "http://localhost:8080")
@GetMapping("/signup")
public register signup(@RequestParam String name) {
    // call the repo ...
}
```

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

## Global CORS configuration

[Source Code](https://stackoverflow.com/questions/44697883/can-you-completely-disable-cors-support-in-spring)  
[Ref](https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application)  

In SpringBoot  
Add `http.cors()` in our WebSecurity Configuration
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // ...

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // add http.cors()
        http.cors().and().csrf().disable().authorizeRequests()
        
    // To enable CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ImmutableList.of("https://www.yourdomain.com"));
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE"));
        // Allowing Cookie 
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));

        
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    
    // oR use this way
     @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }

```

after that we enable CORS as a Configuration
```java
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
}
```



In Spring MVC
```java
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
    }
}
```

## Using it as filter `.Corfilter`

[Source Code](https://stackoverflow.com/questions/51720552/enabling-cors-globally-in-spring-boot/51721298)    

Using `CorsFilter` 
```java
@Bean
public CorsFilter corsFilter() {
    final CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    // Don't do this in production, use a proper list  of allowed origins
    config.setAllowedOrigins(Collections.singletonList("*"));
    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
    
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
```


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


## implements `Filter` to set up response's header
```java
@Component
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept");
        response.setHeader("Access-Control-Expose-Headers", "Location");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //...
    }
}
```

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

### CORS spring security `filter` vs `WebMvcConfigurer.addCorsMappings`

[Reference](https://stackoverflow.com/questions/63426010/cors-spring-security-filter-vs-webmvcconfigurer-addcorsmappings)

#### WebMvcConfigurer

`WebMvcConfigurer` is part of the Spring Web MVC library.   
Configuring CORS with `addCorsMappings` adds CORS to all URLs which are handled by Spring Web MVC
- You can't use it for non Spring Web MVC URLs like JSF, Servlet, JAX-WS, JAX-RS, ...

`WebSecurityConfigurerAdapter` is part of the Spring Security library.  
Configuring CORS with `cors()` adds CORS to all URLs which are handled by Spring Security(to ensure that CORS requests are handled first)


#### CorsFilter

The easiest way to ensure that CORS is handled first is to use the `CorsFilter`.
If you are using Spring Web MVC and Spring Security together you can share the configuration

If you are using Spring MVC’s CORS support, you can omit specifying the `CorsConfigurationSource` and Spring Security will leverage the CORS configuration provided to Spring MVC.

