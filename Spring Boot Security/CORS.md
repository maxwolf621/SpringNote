###### tags: `Spring`
# Spring CORS
[What is CORS](https://shubo.io/what-is-cors/)  
[Spring CORS](https://spring.io/guides/gs/rest-service-cors/#controller-method-cors-configuration)  
There are 3 way to configure(disable) Spring boot CORS

### 1. controller-level CORS configuration
```java
@CrossOrigin(origins = "http://localhost:8080")
@GetMapping("/signup")
public register signup(@RequestParam String name) {
    // call the repo ...
}
```

### 2. Global CORS configuration

[Source Code](https://stackoverflow.com/questions/44697883/can-you-completely-disable-cors-support-in-spring)  
[Ref](https://stackoverflow.com/questions/36968963/how-to-configure-cors-in-a-spring-boot-spring-security-application)  

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


Enable CORS as a Configuration
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

### 3. Using it as filter

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

or implements Filter to set up response's header
```java=
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

    }
}
```

## CORS spring security filter vs WebMvcConfigurer.addCorsMappings


[Ref](https://stackoverflow.com/questions/63426010/cors-spring-security-filter-vs-webmvcconfigurer-addcorsmappings)

### WebMvcConfigurer

`WebMvcConfigurer` is part of the Spring Web MVC library.   
Configuring CORS with `addCorsMappings` adds CORS to all URLs which are handled by Spring Web MVC
- You can't use it for non Spring Web MVC URLs like JSF, Servlet, JAX-WS, JAX-RS, ...

`WebSecurityConfigurerAdapter` is part of the Spring Security library.  
Configuring CORS with `cors()` adds CORS to all URLs which are handled by Spring Security(to ensure that CORS requests are handled first)


### CorsFilter

The easiest way to ensure that CORS is handled first is to use the `CorsFilter`.
If you are using Spring Web MVC and Spring Security together you can share the configuration

If you are using Spring MVC’s CORS support, you can omit specifying the CorsConfigurationSource and Spring Security will leverage the CORS configuration provided to Spring MVC.