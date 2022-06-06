###### tags: `Spring`

# Spring CORS
- [What is CORS](https://shubo.io/what-is-cors/)  
- [Spring CORS](https://spring.io/guides/gs/rest-service-cors/#controller-method-cors-configuration)  
- [How to configure CORS in spring security](https://www.tpisoftware.com/tpu/articleDetails/1415)     
- [Cors SetUp Baeldung](https://www.baeldung.com/spring-cors)          


![image](https://user-images.githubusercontent.com/68631186/172223997-f97d8984-2d9d-4b01-b686-a147e1e9dd7d.png)


There are 3 way to configure CORS in Spring boot 

##  controller-level CORS configuration with `@CrossOrigin` anootation

`@CrossOrigin` with Method
- For example allowing access from origin named `localhost:8080`
```java
@CrossOrigin(origins = "http://localhost:8080")
@GetMapping("/signup")
public register signup(@RequestParam String name) {
    // call the repo ...
}
```

`CrossOrigin` with `@Controller` Class 
```java
/**
  * Allow all origins access {@code remove} 
  * Allow {@code http://example.com} to access {@code retrieve}
  * Both methods have maxAge of 3,600 second
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

- [Source Code](https://stackoverflow.com/questions/44697883/can-you-completely-disable-cors-support-in-spring)  
- [[StackOverflow]How to Configure cors in a spring boot](https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application)  


Configure HttpSecurity with `.cors().disable()` to disable cors configuration in WebSecurity Configuration
and we can configure CORS settings via `CorsConfigurationSource`+`WebMvcConfigurer` or `WebMvcConfigurer`
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // ...

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // add http.cors()
        http.cors()
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
        
    // configure CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(ImmutableList.of("https://www.yourdomain.com"));
        configuration.setAllowedMethods(ImmutableList.of("GET", "POST", "PUT", "DELETE"));
        
        // Cookies 
        configuration.setAllowCredentials(true);
        
        configuration.setAllowedHeaders(ImmutableList.of("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    
    // instead of CorsConfigurationSource 
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

If we configure cors with `CorsConfigurationSource` 
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

Spring MVC
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

## `@Bean` CorsFilter 

![image](https://user-images.githubusercontent.com/68631186/172229064-cde45215-5c83-4f63-a500-bbe42b625daf.png)

[Source Code](https://stackoverflow.com/questions/51720552/enabling-cors-globally-in-spring-boot/51721298)    
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

        config.addAllowedOrigin("/*");

        // allow origin carry cookies (information)
        config.setAllowCredentials(true);

        // allow all methods in header 
        config.addAllowedMethod("/*");
        //config.setAllowedMethods(Arrays.asList("GET", "PUT", "POST","DELETE"));
        //config.addAllowedMethod(HttpMethod.POST);

        config.addAllowedHeader("/*");
        //config.addAllowedHeader("x-firebase-auth");

        // 可獲取哪些Header（因為跨網域預設不能取得全部Header資訊）
        config.addExposedHeader("/*");
        //config.addExposedHeader("Content-Type");
        //config.addExposedHeader( "X-Requested-With");
        //config.addExposedHeader("accept");
        //config.addExposedHeader("Origin");
        //config.addExposedHeader( "Access-Control-Request-Method");
        //config.addExposedHeader("Access-Control-Request-Headers");


        // 映射路徑
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        // return CorsFilter object configuring 
            with UrlBasedCorsConfigurationSource
        return new CorsFilter(configSource);
    }

}
```

## `Filter` implementation 

```java
@Component
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    // Confiture Cors Filter
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

## CorsFilter vs WebMvcConfigurer#addCorsMappings

- [[StackOverflow]cors-spring-security-filter](https://stackoverflow.com/questions/63426010/cors-spring-security-filter-vs-webmvcconfigurer-addcorsmappings)

### WebMvcConfigurer

`WebMvcConfigurer` is part of the Spring Web MVC library.   
Configuring CORS with `addCorsMappings` adds CORS to all URLs which are handled by Spring Web MVC which menas you can't use it for non Spring Web MVC URLs like JSF, Servlet, JAX-WS, JAX-RS, ...

### WebSecurityConfigurerAdapter

`WebSecurityConfigurerAdapter` is part of the Spring Security library.  
Configuring CORS with `HttpSecurity#cors()` adds CORS to all URLs which are handled by Spring Security(to ensure that CORS requests are handled first)

### CorsFilter

The easiest way to ensure that CORS is handled first is to use the `CorsFilter`.    
**If you are using Spring Web MVC and Spring Security together you can share the configuration**   
If you are using Spring MVC’s CORS support, you can omit specifying the `CorsConfigurationSource` and Spring Security will leverage the CORS configuration provided to Spring MVC.

