###### tags: `Spring`
# `UserDetailsService` and `WebConfiguer`

## Security Configuration

[Source](https://stackoverflow.com/questions/35218354/difference-between-registerglobal-configure-configureglobal-configureglo)


We can have different Security Configuration via `WebSecurityConfigurerAdapters`, and also limit some of `WebSecurityConfigurerAdapters` for some specific Authentications

- Security Configuration means
    > To determine how you want to authenticate for the request from client

So there are two main methods
1. Authentications that **are** Available for all security applications 
2. Authentication that **is** only available for a certain application 


### 1. Global Authentication with Annotation `@Autowired`

To configuration such Authentication
1. Annotation MUST be in a class annotated with one of the following : `@EnableWebSecurity`, `@EnableWebMvcSecurity`, `@EnableGlobalMethodSecurity`, or `@EnableGlobalAuthentication`
2. Configure A method with annotation `@Autowired` and having `AuthenticationManagerBuilder` as pramater for this method

These methods are often named as `registerGlobal, configureGlobal, configureGlobalSecurity`

This means you are applying this method to be used(shared) by **Entire Application** (也就是說for all the other `WebSecurityConfigurerAdapters`)  
> (i.e. other WebSecurityConfigurerAdapter instances, method security, etc) [soruce](https://github.com/spring-projects/spring-security/issues/4571)


The Method would look like this
```java
@EnableWebSecurity
public class MyConfiguration {
    @Autowired
    public void (AuthenticationManagerBuilder auth) throws Exception {
        /**
         * @Description AuthenticationConfigure
         *     How to configure our Authentication Provider
         *     如何認證方式（需要user name, email or password 等等)
         */
        auth.AuthenticationConfigure( ... );
}
```

For example  
To Apply this Authentication Method via `jdbcAuthentication` to all `WebSecurityConfigurerAdapters`

```java
/**
 * <p> code reference </p> 
 * {@link https://stackoverflow.com/questions/32035703/security-config-configureglobal}
 */
@Autowired
private DataSource dataSource;

/**
 *  @Description
 *   We have a authentication method
 *   via {@code jdbcAutehtication}
 *   這個認證Method are applied for all securityConfiguration 
 *   with annotation <pre> @EnableWebSecurity </pre>
 */
@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
            .dataSource(dataSource)
            .withDefaultSchema()
            .withUser("user").password("password").roles("USER").and()
            .withUser("admin").password("password").roles("USER", "ADMIN");
}
```
By default `jdbcAuthentication` expects that:
```sql=
select username, password, enabled from users where username = ?
```

### 2. Specific Authentication

we need to create A specific `WebSecurityConfigurerAdapter` implementation to use the specific AuthenticationManager.
> Overriding configure is a convenient approach in a subclass of `WebSecurityConfigurerAdapter` (or any `@Configuration` class implementing `WebSecurityConfigurer`)


- The protected configure is like an anonymous inner bean where the scope is limited to that of this `WebSecurityConfigurerAdapter`.
    > like Specific security `@Configuration` class
- If we need it **exposed** as a Bean, we can use `authenticationManagerBean.`
    > Alternatively, we can also just expose one of a `UserDetailsService`, `AuthenticationProvider`, or `AuthenticationManger` as a Bean.

```java
/**
 * <p> 
 * This In Memory Authentication only applied <strong> provide </strong> 
 * for A SPECIFIC authenticationManager 
 * </p>
 */
@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
          .withUser("user").password("password").roles("USER").and()
          .withUser("admin").password("password").roles("USER", "ADMIN");
    }
}
```

## `UserdetailsService`
[Source](https://stackoverflow.com/questions/64526372/when-should-i-override-the-configureauthenticationmanagerbuilder-auth-from-spr)  
UserDetailsService is used by `DaoAuthenticationProvider` for retrieving a username, password, and other attributes from an protected Resource  
![](https://i.imgur.com/WnXq9Hy.png)  

As the above example we know Spring Security provides in-memory and JDBC implementations of `UserDetailsService`.  
We also can define custom authentication by exposing a custom `UserDetailsService` implementation as a bean in the `websecurityconfigurerAdapter` implementation  

- The `UserDetailsService` interface is used to retrieve user-related data. 
    > It has one method named `loadUserByUsername()` which can be overridden to customize the process of finding the related user protected resource from server. 
    > `loadUserByUsername(String username)` returns default datatype `UserDetails` which is part of `org.springframework.security.core.userdetails.User` which consists of `getUsername()`, `getPassword()`, `getAuthorities()` methods which is used further for spring security 
- We can also customize the `UserDetails` using `org.springframework.security.core.userdetails.User` by implementing the `UserDetails` interface.

```java
/**
 * <p> UserDetailsService Implementation </p>
 * To retrieve the user protected resource from server
 */
@Service
@AllargsConstructor
public class UserRepositoryUserDetailsService implements UserDetailsService {
    
    // Portected Resource
    private final UserRepository userRepository;

    // DI without annotation @AllargsConstructor
    //public UserRepositoryUserDetailsService(UserRepository userRepository) {
    //    this.userRepository = userRepository;
    //

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = userRepository.findByEmail(username);
        if(user == null) {
            throw new UsernameNotFoundException("Could not find user " + username);
        }
        List<GrantedAuthority> authorities = convert(user.getRoles());
        /**
         * @return
         *   {@link org.springframework.security.core.userdetails.User}
         */
        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
```

we can configure this custom authentication in `WebsecurityConfigurerAapter`
```java
@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsPrincipalService userDetailsPrincipalService;
    /**
     * @Description
     * 建立一種認證方式利用UserDetailsPrincipalService提供的認證方式
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsPrincipalService userDetailsPrincipalService) throws Exception {
        auth.userDetailsService(userDetailsPrincipalService);
    }
    
    //The rest
}
```
