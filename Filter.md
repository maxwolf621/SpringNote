# LongIn In Action

![](https://i.imgur.com/ryVkWvt.png)
![](https://i.imgur.com/8PopRQG.png)

- 一般而言當驗證的請求送至Spring Security後，請求中的username與password會在`UsernamePasswordAuthenticationFilter.attemptAuthentication(HttpServletRequest request, HttpServletResponse response)`中被放入`UsernamePasswordAuthenticationToken`的instance，然後交給`AuthenticationManager.authenticate(Authentication authentication)`進行驗證，而實際上最終是轉交給`AuthenticationProvider.authenticate(Authentication authentication)`驗證；若驗證成功則會返回完整使用者資訊的Authentication instance

## UsernamePasswordAuthenticationToken

UsernamePasswordAuthenticationToken is an Authentication implementation that is designed for simple presentation of a username and password.


![](https://i.imgur.com/U0WFL88.png)


### Relationship `UserDetails` and `UsernamePasswordAuthenticationToken` 
- [SourceCode of `UserNamePasswordAuthenticationToken`](https://github.com/spring-projects/spring-security/blob/master/core/src/main/java/org/springframework/security/authentication/UsernamePasswordAuthenticationToken.java)
- [SourceCode](https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/userdetails/UserDetails.java)

![](https://i.imgur.com/UPaxj2r.png)

### Structure of `UsernamePasswordAuthenticationToken`
![](https://i.imgur.com/2eMlrC2.png)

```java
/**
 * This constructor should only be used by
 * <code>AuthenticationManager</code> 
 * or
 * <code>AuthenticationProvider</code> 
 * implementations that are satisfied with producing a trusted 
 * (i.e. {@link #isAuthenticated()} = <code>true</code>)
 * authentication token.
 * @param principal (Who)
 * @param credentials (Which user) (Authentication of the user)
 * @param authorities (What user can do)
 */
public UsernamePasswordAuthenticationToken(Object principal, Object credentials,
        Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    // Authenticated
    super.setAuthenticated(true);
}
```

#### `StringUtils.hasText(String words)` 

```java
StringUtils.hasText(null) = false
StringUtils.hasText("") = false
StringUtils.hasText(" ") = false
StringUtils.hasText("12345") = true
StringUtils.hasText(" 12345 ") = true
```



## `WebSecurityConfigurerAdapter` implementation with customized `Filter` implementation

Using `addFilterBefore` add our customized filter
```java
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated();
        // with our customized filter
        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // inject the authenticationManagerBuilder bean
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                                    .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
-  `configureGlobal` method should be `@Autowired` in order to get the `AuthenticationManagerBuilder` bean to create authentication provider
-  `@EnableWebSecurity` doesn't inject beans, it only provides a way to customize the web security application.

- [`configureGlobal`](https://stackoverflow.com/questions/54431305/configuring-authenticationmanagerbuilder-to-use-user-repository)
- [Why use `ConfigureGlobal` instead of `DaoProvider`](https://stackoverflow.com/questions/38924178/spring-security-userdetailsservice-authenticationprovider-pass-word-encoder)

## DaoAuthenticationProvider in WebSecurityConfigurerAdapter implementation

```java 
@Bean(BeanIds.AUTHENTICATION_MANAGER)
@Override
public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
}
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
}

// Method auth.userDetailsService()
public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(T userDetailsService) throws Exception {
    this.defaultUserDetailsService = userDetailsService;
    return apply(new DaoAuthenticationConfigurer<>(userDetailsService));
}

@Bean
public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider =  new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}


```
- The `userDetailsService` method is a shortcut for creating `DaoAuthenticationProvider` bean!  
- **Spring's Security `DaoAuthenticationProvider` is a simple authentication provider that uses a Data Access Object (DAO) to retrieve user information from a relational database. It leverages a `UserDetailsService` (as a DAO) in order to lookup the username, password and GrantedAuthority.**

#### DaoAuthenticationComfigurer
[Source Code](https://github.com/spring-projects/spring-security/blob/e51ca79954096c0897a730ff48367051f6fa8387/config/src/main/java/org/springframework/security/config/annotation/authentication/configurers/userdetails/DaoAuthenticationConfigurer.java#L31)
```java
public class DaoAuthenticationConfigurer<B extends ProviderManagerBuilder<B>, U extends UserDetailsService>
		extends AbstractDaoAuthenticationConfigurer<B, DaoAuthenticationConfigurer<B, U>, U> {
	/**
	 * Creates a new instance
	 * @param userDetailsService
	 */
	public DaoAuthenticationConfigurer(U userDetailsService) {
		super(userDetailsService);
	}
```

[DaoAuthenticationConfigurer](https://www.fatalerrors.org/a/in-depth-understanding-of-securityconfigurer.html)
![](https://i.imgur.com/WE3JKDf.png)
- AbstractDaoAuthenticationConfigurer  
In AbstractDaoAuthenticationConfigurer, the main thing is to construct a default DaoAuthentication provider and configure PasswordEncoder and userdetails service for it.
