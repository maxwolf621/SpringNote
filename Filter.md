###### tags: `Spring Project`
# A Login Filter and Authentication
[TOC]

![](https://i.imgur.com/8PopRQG.png)
![](https://i.imgur.com/ryVkWvt.png)

## OnePerRequestFilter

[Explanation](https://stackoverflow.com/questions/13152946/what-is-onceperrequestfilter)
[SourceCode of abstract OncePerRequestFilter](https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/main/java/org/springframework/web/filter/OncePerRequestFilter.java)
[Methods](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html)
[chainFilter.doFilter](https://stackoverflow.com/questions/2057607/what-is-chain-dofilter-doing-in-filter-dofilter-method)
[JWT](/o9RYmd2DR96e4XId64Ao5w)
[Authentication & Authorization](/CrTB3w_mRm-2SFVF648fpw)
[Login Project](/Pi3Ra4aQQ_2h4P8IjVfpJA)
[Operation of Userdetails](https://www.mdeditor.tw/pl/gOR8/zh-tw)
[A login Project](https://blog.csdn.net/weixin_44516305/article/details/88868791)

![](https://i.imgur.com/br6ByIm.png)

```java
public abstract class OncePerRequestFilter extends GenericFilterBean {

    /**
     * Suffix that gets appended to the filter name for the
     * "already filtered" request attribute.
     */
    public static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

    /**
     * This {@code doFilter} implementation stores a request attribute for
     * "already filtered", proceeding without filtering again if the
     * attribute is already there.
     */
    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // check request and response dataType
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("OncePerRequestFilter just supports HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // check if this request has Already filtered via request.getAttribute method
        String alreadyFilteredAttributeName = getAlreadyFilteredAttributeName();
        boolean hasAlreadyFilteredAttribute = request.getAttribute(alreadyFilteredAttributeName) != null;

        if (skipDispatch(httpRequest) || shouldNotFilter(httpRequest)) {
            // Proceed next filter in the rest of filterChain
            filterChain.doFilter(request, response);
        }
        else if (hasAlreadyFilteredAttribute) {
            if (DispatcherType.ERROR.equals(request.getDispatcherType())) {
                doFilterNestedErrorDispatch(httpRequest, httpResponse, filterChain);
                return;
            }
            // Proceed without invoking this filter...
            filterChain.doFilter(request, response);
        }
        else {
            // This request has not filtered yet
            //    set the request as hasalreadyfiltered via .setAttribute method
            request.setAttribute(alreadyFilteredAttributeName, Boolean.TRUE);
            try {
                // we `override` this method to process once filter
                doFilterInternal(httpRequest, httpResponse, filterChain);
            }
            finally {
                // Remove the "already filtered" request attribute for this request.
                request.removeAttribute(alreadyFilteredAttributeName);
            }
        }
    }

    private boolean skipDispatch(HttpServletRequest request) {
        if (isAsyncDispatch(request) && shouldNotFilterAsyncDispatch()) {
            return true;
        }
        if (request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) != null && shouldNotFilterErrorDispatch()) {
            return true;
        }
        return false;
    }

    //A filter can be invoked in more than one thread 
    //    over the course of a single request.
    protected boolean isAsyncDispatch(HttpServletRequest request) {
        return DispatcherType.ASYNC.equals(request.getDispatcherType());
    }

    /**
     * response will not be committed after the current thread is exited.
     * @param request the current request
     */
    protected boolean isAsyncStarted(HttpServletRequest request) {
        return WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted();
    }

    /**
    * Return the name of the request attribute that identifies that a request
    * is already filtered.
    * <p>The default implementation takes the configured name of the concrete filter
    * instance and appends ".FILTERED". If the filter is not fully initialized,
    * it falls back to its class name.
    */
    protected String getAlreadyFilteredAttributeName() {
    String name = getFilterName();
    if (name == null) {
        name = getClass().getName();
    }
    return name + ALREADY_FILTERED_SUFFIX;
    }

    /**
     * It Can be overridden in subclasses for custom filtering control,
     * returning {@code true} to avoid filtering of the given request.
     * <p>The default implementation always returns {@code false}.
     * @param request current HTTP request
     * @return whether the given request should <i>not</i> be filtered
     * @throws ServletException in case of errors
     */
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    /**
     - A filter can be invoked in more than one thread 
       over the course of a single request.
      */
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }
    /**
     - Whether to filter error dispatches 
       such as when the servlet container processes and error mapped in {@code web.xml}. 
     - The default return value is "true", 
       which means the filter will not be invoked in case of an error dispatch.
     */
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }


    /**        We always override this methood
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     */
    protected abstract void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException;

    // Whether to filter error dispatches such as when the servlet container processes and error mapped in web.xml.
    protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);
    }

}
```

- `void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)`
    > This doFilter implementation stores a request attribute for "already filtered", proceeding without filtering again if the attribute is already there.

- `protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`
    > **Same contract as for doFilter, but guaranteed to be just invoked once per request within a single request thread**.

- `protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`
    > Typically an ERROR dispatch happens after the REQUEST dispatch completes, and the filter chain starts anew.

- `protected String getAlreadyFilteredAttributeName()`
    > Return the name of the request attribute that identifies that a request is already filtered.

- `protected boolean isAsyncDispatch(HttpServletRequest request)`
    > a filter can be invoked in more than one thread over the course of a single request.
- `protected boolean isAsyncStarted(HttpServletRequest request)`
    > Whether request processing is in asynchronous mode meaning that the response will not be committed after the current thread is exited(結束).
- `protected boolean shouldNotFilter(HttpServletRequest request)`
    > Can be overridden in subclasses for custom filtering control, returning true to avoid filtering of the given request.
- `protected boolean shouldNotFilterAsyncDispatch()`
    > The dispatcher type **javax.servlet.DispatcherType.ASYNC** (Dispatcher Servlet 3.0)  
    > means ==a filter can be invoked in more than one thread== over the course of a single request.
- `protected boolean shouldNotFilterErrorDispatch()`
    > Whether to filter error dispatches such as when the servlet container processes and error mapped in `web.xml`.

## Filter the request via customized class extending `OncePerRequestFilter`
To valid the token from client we need to customize the method `doFilterInternal` in `JwtAuthenticationFilter` extends `OncePerRequestFilter`
[SourceCode of UserNamePasswordAuthenticationToken](https://github.com/spring-projects/spring-security/blob/master/core/src/main/java/org/springframework/security/authentication/UsernamePasswordAuthenticationToken.java)

```java=
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException 
    {
        // get Token from request 
        String jwToken = getJwtFromRequest(request);

        // Check validate of token and parse it
        if (StringUtils.hasText(jwToken) && jwtProvider.validateToken(jwToken)) {
            // using Token to find the user
            String username = jwtProvider.getUsernameFromJWT(jwToken);
            // get User details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Create a UsernamePasswordAuthenticationToken to make the Authentication
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                                    userDetails,null, 
                                    userDetails.getAuthorities()
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // go next filter in the filter chain
        filterChain.doFilter(request, response);
    }

    //**  Get client's Token from Request
    private String getJwtFromRequest(HttpServletRequest request) {
        // in json {"Authorization" : "Bearer <token>"}
        String bearerToken = request.getHeader("Authorization");
        // first check if token null or not
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // get character starts from bearerToken[7]
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
```

## Relationship `UserDetails` and `UsernamePasswordAuthenticaitonToken` 
![](https://i.imgur.com/UPaxj2r.png)

#### Userdetails

UserDetails will fetch the authenticated user via Authentication Provider and return a userDetails value

[SourceCode](https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/userdetails/UserDetails.java)

```java=
public interface UserDetails extends Serializable {

	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	Collection<? extends GrantedAuthority> getAuthorities();

	/**
	 * Returns the password used to authenticate the user.
	 * @return the password
	 */
	String getPassword();

	/**
	 * Returns the username used to authenticate the user. Cannot return
	 * <code>null</code>.
	 * @return the username (never <code>null</code>)
	 */
	String getUsername();

	/**
	 * Indicates whether the user's account has expired. 
	 * An expired account cannot be authenticated.
	 * @return <code>true</code> if the user's account is valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	boolean isAccountNonExpired();

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be
	 * authenticated.
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	boolean isAccountNonLocked();

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired
	 * credentials prevent authentication.
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	boolean isCredentialsNonExpired();

	/**
	 * Indicates whether the user is enabled or disabled. 
	 * disabled user cannot be authenticated.
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	boolean isEnabled();

}
```

used by interface UserDetailsService (Read-Only)
[SorceCode](https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/core/userdetails/UserDetailsService.java)
```java=
public interface UserDetailsService {

	/**
	 * Locates the user based on the username. 
	 * In the actual implementation, the search may possibly be case sensitive, 
	 * or case insensitive depending on how the
	 * implementation instance is configured. In this case, the <code>UserDetails</code>
	 * object that comes back may have a username that is of a different case than what
	 * was actually requested..
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 * GrantedAuthority
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
```

### Structure of `UsernamePasswordAuthenticationToken`
![](https://i.imgur.com/2eMlrC2.png)

- An Authentication implementation that is designed for simple presentation of a username and password.


```java=
/**
 * This constructor should only be used by
 * <code>AuthenticationManager</code> 
 * or
 * <code>AuthenticationProvider</code> 
 * implementations that are satisfied with producing a trusted 
 * (i.e. {@link #isAuthenticated()} = <code>true</code>)
 * authentication token.
 * @param principal (Who)
 * @param credentials (Which) (Authentication of the user)
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

## Interface Authentication

```java=
public interface Authentication extends Principal, Serializable {

	/**
	 * Set by an <code>AuthenticationManager</code> 
	 * to indicate the authorities that the principal has been granted. 
	 * Note that classes should not rely on this value as
	 * being valid unless it has been set by a trusted <code>AuthenticationManager</code>.
	 * Implementations should ensure that modifications to the returned collection array
	 * @return the authorities granted to the principal, 
	 * or an empty collection 
	 * if the token has not been authenticated. 
	 * Never null.
	 */
	Collection<? extends GrantedAuthority> getAuthorities();

    // id number
	Object getCredentials();
    // ip address ...
	Object getDetails();

	/**
	 * Id card
	 */
	Object getPrincipal();

	/**
	 * Used to indicate to {@code AbstractSecurityInterceptor} whether it should present
	 * the authentication token to the <code>AuthenticationManager</code>. Typically an
	 * <code>AuthenticationManager</code> (or, more often, one of its
	 * <code>AuthenticationProvider</code>s) will return an immutable authentication token
	 * after successful authentication, in which case that token can safely return
	 * <code>true</code> to this method. Returning <code>true</code> will improve
	 * performance, as calling the <code>AuthenticationManager</code> for every request
	 * will no longer be necessary.
	 * <p>
	 * For security reasons, implementations of this interface should be very careful
	 * about returning <code>true</code> from this method unless they are either
	 * immutable, or have some way of ensuring the properties have not been changed since
	 * original creation.
	 * @return true if the token has been authenticated and the
	 * <code>AbstractSecurityInterceptor</code> does not need to present the token to the
	 * <code>AuthenticationManager</code> again for re-authentication.
	 */
	boolean isAuthenticated();

	/**
	 * Implementations should <b>always</b> allow this method to be called with a
	 * <code>false</code> parameter, as this is used by various classes to specify the
	 * authentication token should not be trusted.
	 *  If an implementation wishes to reject
	 * an invocation with a <code>true</code> parameter (which would indicate the
	 */
	void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;

}f
```

- 在安全驗證的概念中，使用系統資源對象的概念可分為Subject，Principal及User
    > [More details](/Pi3Ra4aQQ_2h4P8IjVfpJA)


### Principal

[Methods In Principal](https://docs.oracle.com/javase/6/docs/api/java/security/Principal.html?is-external=true)

interface Principal represents the abstract notion of a principal, which can be used to represent any entity, such as **an individual, a corporation, and a login id.**

- Principle是指一個存取系統資源(database)的實體(Entity)，**簡單來說就是登入合法的使用者**。

:::danger
Principle與User的差異是，==Principal是指一個可辨識的唯一身分(e.g. person with Id card)==，用來代表一個人，一個公司，一個裝置或另一個系統，並不限定於代表一個人；而使用者(User)是指與系統互動的Operator (e.g. client)
:::

- Spring Security中的principal是來自Authentication.getPrincipal()，**回傳值為被驗證或已被驗證的主體**。
    > 在一個帶有使用者名稱(username)及密碼(password)的驗證請求(authentication request)中，principal即為username。

- ==Spring Security通常用UserDetails來封裝principle資訊==


### credentials

- 根據Authentication.getCredentials()的說明，Credential（憑證）是指用來證明Principal身分的東西，通常是密碼(password)。

Spring Security取得credential的方法如下
```java 
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
Object credentials = auth.getCredentials();
String password = (String) credentials;
Authentication.getCredentials()取出的credential是Object型別的物件。
```

:::info
如果是使用Token驗證，通常credential會被包在token裡面。
:::

#### `StringUtils.hasText(String words)` 
```java=
StringUtils.hasText(null) = false
StringUtils.hasText("") = false
StringUtils.hasText(" ") = false
StringUtils.hasText("12345") = true
StringUtils.hasText(" 12345 ") = true
```


- 一般而言當驗證的請求送至Spring Security後，請求中的username與password會在`UsernamePasswordAuthenticationFilter.attemptAuthentication(HttpServletRequest request, HttpServletResponse response)``中被放入UsernamePasswordAuthenticationToken`的instance，然後交給`AuthenticationManager.authenticate(Authentication authentication)`進行驗證，而實際上最終是轉交給`AuthenticationProvider.authenticate(Authentication authentication)`驗證；若驗證成功則會返回完整使用者資訊的Authentication instance

![](https://i.imgur.com/U0WFL88.png)
[More Details](/CrTB3w_mRm-2SFVF648fpw)  

#### Authentication, AuthenticaionManager and AuthenticationProvide

- Simply put, the AuthenticationManager is the main strategy interface for authentication.
    ```java
    public interface AuthenticationManager {
        Authentication authenticate(Authentication authentication)
                                    throws AuthenticationException;
    }
    ```

If the principal of the input authentication is valid and verified, AuthenticationManager#authenticate returns an Authentication instance with the authenticated flag set to true. Otherwise, if the principal is not valid, it will throw an AuthenticationException. For the last case, it returns null if it can't decide.

- ProviderManager is the default implementation of AuthenticationManager. 
    > It delegates the authentication process to a list of AuthenticationProvider instances.


## A class extends WebSecurityConfigurerAdapter with customized filter 

#### WebSecurityConfigurerAdapter
[SourceCode](https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.java)

![](https://i.imgur.com/mRmYugo.png)


```java=
/**
 * Provides a convenient base class for creating a {@link WebSecurityConfigurer} instance.
 * The implementation allows customization by overriding methods.
 *
 * <p>
 * Will automatically apply the result of looking up {@link AbstractHttpConfigurer} from
 * {@link SpringFactoriesLoader} to allow developers to extend the defaults. 
 *
 * @see EnableWebSecurity
 * @author Rob Winch
 */
@Order(100)
public abstract class WebSecurityConfigurerAdapter implements WebSecurityConfigurer<WebSecurity> {
    //..
}
```


Using `addFilterBefore` add our customized filter
```java=
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    // for .addFilterBefroe
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
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                // inject the bean
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
-  `configureGlobal` method should be `@Autowired` in order to get the `AuthenticationManagerBuilder` bean and define the authentication type for the application
-  `@EnableWebSecurity` doesn't inject beans, it only provides a way to customize the web security application.

[configureGlobal](https://stackoverflow.com/questions/54431305/configuring-authenticationmanagerbuilder-to-use-user-repository)
[Why use ConfigureGlobal instead of DaoProvider](https://stackoverflow.com/questions/38924178/spring-security-userdetailsservice-authenticationprovider-pass-word-encoder)

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

.
@Bean
public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider =  new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(encoder());
    return authProvider;
}
```
- The `userDetailsService` method is a shortcut for creating `DaoAuthenticationProvider` bean!  
- **Spring's Security `DaoAuthenticationProvider` is a simple authentication provider that uses a Data Access Object (DAO) to retrieve user information from a relational database. It leverages a UserDetailsService (as a DAO) in order to lookup the username, password and GrantedAuthority s.**

userDetailsService method
```java=
public <T extends UserDetailsService> DaoAuthenticationConfigurer<AuthenticationManagerBuilder, T> userDetailsService(
        T userDetailsService) throws Exception {
    this.defaultUserDetailsService = userDetailsService;
    return apply(new DaoAuthenticationConfigurer<>(userDetailsService));
}
```

#### DaoAuthenticationComfigurer
[Source Code](https://github.com/spring-projects/spring-security/blob/e51ca79954096c0897a730ff48367051f6fa8387/config/src/main/java/org/springframework/security/config/annotation/authentication/configurers/userdetails/DaoAuthenticationConfigurer.java#L31)
```java=
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
    > In abstractdaoauthentication configurer, the main thing is to construct a default daoauthentication provider and configure PasswordEncoder and userdetails service for it.
