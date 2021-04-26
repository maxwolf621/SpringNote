<style>
html,
body, 
.ui-content,
/*Section*/
.ui-toc-dropdown{
    background-color: #1B2631;
    color: #9BCBFC;
}

body > .ui-infobar {
    display: none;
}
.ui-view-area > .ui-infobar {
    display: block ;
    color: #5D6D7E ;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body blockquote{	
    /*#7FFFD4*/
    /*#59FFFF*/
    color: #7FFFD4;
}


/*---FORM---*/
  .form-control {
    background: #333;
    color: #fff;
  }

  .form-control::placeholder,
  .form-control::-webkit-input-placeholder,
  .form-control:-moz-placeholder,
  .form-control::-moz-placeholder,
  .form-control:-ms-input-placeholder {
    color: #eee;
    
  }



/* navbar for bearbeiten, beides, Anzeigen 當游標指向這些物件時的顏色 */
.navbar .btn-group label.btn-default:focus,
.navbar .btn-group label.btn-default:hover {
background-color: #273746;
color: #eee;
border-color: yellow;
}

/* navbar for bearbeiten, beides, Anzeigen while activing */
.navbar .btn-group label.active {
background-color: #555;
color: #eee;
border-color: ;
}

.navbar .btn-group label.active:focus,
.navbar .btn-group label.active:hover {
background-color: #555;
color: #eee;
border-color: #555;
}

.navbar-default .btn-link:focus,
.navbar-default .btn-link:hover {
    color: #eee;
}

.navbar-default .navbar-nav>.open>a,
.navbar-default .navbar-nav>.open>a:focus,
.navbar-default .navbar-nav>.open>a:hover {
background-color: #555;
}

.dropdown-header {
color: #aaa;
}

.dropdown-menu {
background-color: #222;
border: 1px solid #555;
border-top: none;
}
.dropdown-menu>li>a {
color: #eee;
}

.dropdown-menu>li>a:focus,
.dropdown-menu>li>a:hover {
background-color: #555555;
color: #eee;
}


/* > */
.markdown-body blockquote {
color: #9BCBFC ;
border-left-color: #B22222 ;
font-size: 16px;
}

.markdown-body h6{
    color: gold;
}
.markdown-body h1,
.markdown-body h2 {
    border-bottom-color: #5D6D7E;
    border-bottom-style: ;
    border-bottom-width: 3px;
}

.markdown-body h1 .octicon-link,
.markdown-body h2 .octicon-link,
.markdown-body h3 .octicon-link,
.markdown-body h4 .octicon-link,
.markdown-body h5 .octicon-link,
.markdown-body h6 .octicon-link {
    color: yellow;
}

.markdown-body img {
    background-color: transparent;
}

.ui-toc-dropdown .nav>.active:focus>a, .ui-toc-dropdown .nav>.active:hover>a, .ui-toc-dropdown .nav>.active>a {
    color: gold;
    border-left: 2px solid white;
}

.expand-toggle:hover, 
.expand-toggle:focus, 
.back-to-top:hover, 
.back-to-top:focus, 
.go-to-bottom:hover, 
.go-to-bottom:focus {
    color: gold;
}

/* [](htpp://) */
a,.open-files-container li.selected a {
    color: #D2B4DE;
}
/* == == */
.markdown-body mark,
mark 
{
    background-color: #708090 !important ;
    color: gold;
    margin: .1em;
    padding: .1em .2em;
    font-family: Helvetica;
}
/* `` */
.markdown-body code,
.markdown-body tt {
    color: #eee;
    background-color: #424a55;
}
/* ``` ``` */
.markdown-body pre {
background-color: #1e1e1e;
border: 1px solid #555 !important;
color: #dfdfdf;
}

/* scroll bar */
.ui-edit-area .ui-resizable-handle.ui-resizable-e {
background-color: #303030;
border: 1px solid #303030;
box-shadow: none;
}

.token.char {
color: #7ec699;
}

.token.variable {
color: #BD63C5;
}

.token.regex {
color: #d16969;
}

.token.operator {
color: #DCDCDC;
background: transparent;
}

.token.url {
color: #67cdcc;
}

.token.important,
.token.bold {
font-weight: bold;
}

.token.italic {
font-style: italic;
}

.token.entity {
cursor: help;
}

.token.inserted {
color: green;
}
</style>

###### tags: `Spring`
# Authentication & Authorization
[TOC]

[Type of Authentication](https://blog.csdn.net/gdp12315_gu/article/details/79905424)
[UserDetailsService](https://www.javadevjournal.com/spring-security/spring-security-authentication-providers/)
[reference](https://matthung0807.blogspot.com/2019/09/spring-security-userdetailsservice.html)
[Definition of User, Pricipal, Subject](https://matthung0807.blogspot.com/2018/03/spring-security-principal.html)
[Authentication in HTTP format ](https://blog.csdn.net/kiwi_coder/article/details/28677651)

## Definitions of Authentication and Authorization

[Reference](https://blog.csdn.net/kiwi_coder/article/details/28677651)

- Authentication 
     > To prove who are you
- Authorization
    > To give a permission  (thing you can do, thing you cant do)


### Definitions for Authentication 

- Subject 
    > **In a security context, a subject is any entity that requests access to an object**. 
    > These are generic terms used to denote the thing requesting access and the thing the request is made against.  
    > **When you log onto an application you are the subject and the application is the object**.  
    >>for example when someone knocks on your door the visitor is the subject requesting access and your home is the object access is requested of.

- Principal 
    >**A subset of subject that is represented by an account, role or other unique identifier.** 
    >> When we get to the level of implementation details, **principals are the unique keys we use in access control lists.**  
    > *They may represent human users, automation, applications, connections, etc.*

- User 
    > **A subset of principal usually referring to a human operator.**
    > The distinction is blurring over time because the words "user" or "user ID" are commonly interchanged with "account".
    >  However, when you need to make the distinction between the broad class of things that are principals and the subset of these that are interactive operators driving transactions in a non-deterministic fashion, "user" is the right word.

## Object Authentication

Spring Security 使用一個 Authentication 物件來描述當前使用者的相關資訊，而 SecurityContext 持有的是代表當前使用者相關資訊的 Authentication 的引用。

這個 Authentication 物件不需要我們自己去建立，在與系統互動的過程中，Spring Security 會自動為我們建立相應的 Authentication 物件(via `UserNamePasswordAuthenticationToken`)，然後賦值給當前的 SecurityContext。

To get a Authentication User from Spring Security using `.getPrincipal()`
```java=
//Spring Security中的principal is from
//    Authentication.getPrincipal()，
//        回傳值為被驗證或已被驗證的主體
Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
if (principal instanceof UserDetails) {
    String username = ((UserDetails)principal).getUsername();
} else {
    String username = principal.toString();
}
```
> `.getContext()` : get Current Security Context  
> `.getAuthentication()`: Obtains the currently **authenticated principal**, or an authentication **request token**

The identity of the principal being authenticated. 
:::info
- **Many of the ==authentication providers== will create a `UserDetails` object as the principal.(so we can access the databse)**  

- **The `AuthenticationManager` implementation will often return an Authentication(object Authentication) containing richer information as the principal for use by the application**
:::

[More about User's Authorization](/Pi3Ra4aQQ_2h4P8IjVfpJA)

## HTTP basic's Authentication

Request directly tells the server client's password and username via Header like this
```json=
 GET /secret HTTP/1.1
 Authorization: Basic QWxpY2U6MTIzNDU2
```
> `QWxpY2U6MTIzNDU2` is Base64encode(password and username)

Response if password and username is correct
```json=
 HTTP/1.1 200 OK
```
else
```json=
HTTP/1.1 401 Bad credentials
WWW-Authenticate: Basic realm="Spring Security Application"
```

:::danger
Base64 encode is easy to crack (for example Replay attack ...)
:::

## HTTP Digest

Process
1. Client acceess the sever without any information
2. Server will response with `nonce` to client
3. Client recive the response from Server 
    > Take `nonce` to combine with her/his password and username 
    > Encrypt them via `MD5`
    > Send http request to Server to authenticate
```
client -------- request1:GET ------->>Server

client <<------ response1:nonce ------Server

client ---- request2:Digest Auth --->>Server

client <<------- response2:OK --------Server
```

In request1
>> No password and username involving
```json=
GET /secret HTTP/1.1
    ......
```

#### In response1
Server will respond a `nonce` to client
```json=
HTTP/1.1 401 Full authentication is required to access this resource
WWW-Authenticate: Digest realm="Contacts Realm via Digest Authentication", qop="auth",nonce="MTQwMTk3OTkwMDkxMzo3MjdjNDM2NTYzMTU2NTA2NWEzOWU2NzBlNzhmMjkwOA=="
```
> `401` means server didn't receive any useful information  

#### In request2
Client will put password or other important information with nonce together and encrypt it via MD5
```json=
HTTP/1.1 200 OK
...
...
```


`nonce` is random values, so each time client accesses server, the `norce` should be different.

If client sends the request with same `norce` as last time, server will reject client's request, this means a hacker cracks client's request, he/her is not able to pretend as the client to access the server (Replay Attack)

## Security Builder
[Good Reference](https://medium.com/@yovan/spring-security-configuration-architecture-c9694435330a)
[Reference](https://www.javadevjournal.com/spring-security/spring-security-login/)
`SecurityBuilder` : build up `SecurityConfigurer` Set

![](https://i.imgur.com/S8nhgsZ.png)


- What we need Security Builder?
    > To build up out filter (chains) via `SecurityConfigurer` Set
    > ##### Filter Chain 
    > ![](https://i.imgur.com/Y1VV0zM.png)


There are THREE Security Builders that are provided by Spring Security
1. `WebSecurity`
    >Each  `WebSecurityConfigurerAdapter` will create a Filter (To form a Filter Chain)  
    >Each `WebSecurityConfigurerAdapter` will create a new `HttpSecurity`
    >![](https://i.imgur.com/nLBXbID.png)


2. `HttpSecurity`
    > As illustration, it contains different Security Configurers to from a Security Configuere Set
3. `AuthenticationManagerBuilder`
    > Via InMemory、Jdbc or Ldap configurer，to build a Authentication


### WebSecurityConfigurerAdapter

[More details](/Pi3Ra4aQQ_2h4P8IjVfpJA)
 
> `WebSecurityConfigurerAdapter` provides a set of methods to enable specific web security configuration via different Security Builders(e.g HttpSecurity ...) .

For example
```java=
//it's enable spring security supports 
//    with support for the Spring MVC integration.
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {
    //configute()  method is used to configure distinct security points 
    //    for our application (e.g. secure and non-secure urls, success handlers etc.).
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            //Login and register page to be accessible without any login.
            //Permitting it to all users using the antMatchers pattern.
            .antMatchers("/login", "/register")
            .permitAll()
            //Allowing only logged is customer to access URLs matching with pattern `/account/**`.
            //     Looking for a certain role before allowing the user to access the URL.
            .antMatchers("/account/**").access("hasRole('ROLE_ADMIN')")
            .and()
            .formLogin(form - > form
                .loginPage("/login")
                // A successful authentication, then redirect to
                .defaultSuccessUrl("/home")
                // if login fails, then redirect to 
                .failureUrl("/login?error=true")
            );
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers("/resources/**", "/static/**");
    }
}
```
:::info
When clients send Request, it will be filtered by FilterSecurityInterceptor Filter with `WebSecurityConfigurerAdapter`.

> Once the Request's URL that requires the authentication則會從SecurityContextHolder 取得這 User 的Authentication，判斷是否已經認證過，決定能不能 access。
:::
 
## Interface UserDetailsService

![](https://i.imgur.com/wddCsgT.png)
![](https://i.imgur.com/If4HFFG.png)

- Spring Security makes the Authentication of Client's Request using `ProviderManager` that implements `AuthenticationManager`.  
- `ProviderManager` then *delegates* the numbers of `AuthticationProvider`**s** to do the Authentication

==Authentication provider's implementations are responsible to perform a specific authentication==
```java=
public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication) throws AuthenticationException;
    boolean supports(Class<?> authentication);
}
```
> There are many different `AuthenticationProvider` implementations (e.g. `DaoAuthenticationProvider` ... ) for a specific authentication
>> for example `DaoAuthenticationProvider` (dependency)uses the `UserDetailsService` to retrieve user information a username and password.  
  
```java=
public interface UserDetailsService {
    // var1 as login id
    UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
}
```
> `loadUserByUsername` : using `UserDetails` and `JpaRepository` to query database to compare with information from client's request

### Customize Authentication Provider implements UserDetailsService

so we can define **custom authentication** by exposing a custom `UserDetailsService` as a bean(if we dont wnat to use Authentication Providers like DaoAuthenticationProvider ... )
> Our custom user service can load user based on our data model (Data Base).  
```java=
@Service
public class CustomUserDetailService implements UserDetailsService {

    // Repository or Service 
    @Autowired
    UserRepository userRepository;

    @Override
    // String emial as our login iD
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final UserEntity customer = userRepository.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException(email);
        }
        // fetch from database using UserDetails
        UserDetails user = User.withUsername(customer.getEmail())
                            .password(customer.getPassword())
                            .authorities("USER").build();
        return user;
    }
}
```
> we need `JPA/CRUD Repository` to help `UserDetails` fetches user information from data base for comparing validation of client's request


### Authentication flow

![](https://i.imgur.com/aLKSado.png)
1. Server creates (via filter) an `UsernamePasswordAuthenticationToken` from (request containing token) the supplied username and password.
2. It passes the token(get from request) to the *Authentication Manager.*
3. The *Provider Manager* will **delegate** the authentication to the `DaoAuthenticationProvider` (for thie diargram) including the *authentication token*.
4. The `DaoAuthenticationProvider` use the custom `UserDetailsService` service to get the *user details* information from the database.
3. On successful authentication, the authentication object will contain the fully populated object including the authorities details.
4. **The returned `UsernamePasswordAuthenticationToken` will be set on the `SecurityContextHolder` by the authentication Filter.**


## The Login Workflow

[Note For UsernamePasswordAuthenticationFilter](/WClD1mCtTcqmt2boXxGR7A)

![](https://i.imgur.com/Lv83VUC.png)
1. Client fills out the credentials on the login page.
2. ==On form submission, the `UsernamePasswordAuthenticationFilter` creates a `UsernamePasswordAuthenticationToken` by extracting the username and password from the (`URL`) request parameters.==
3. The `AuthenticationManager` is responsible to validate the user based on the supplied credentials 
4. If authenticated, Spring security performs several additional operations. 
    - `SessionAuthenticationStrategy` is notified for new login.  
    This handles the HTTP session and makes sure a valid session exists and handles any against session-fixation attacks.
    - Spring security stores the user authentication details in the `SecurityContextHolder`. 
        > It will update the `SecurityContextHolder` with authentication details.
    - If `RememberMeServices` service is active, it will activate the `loginSuccess` method. 
    - It will publish an `InteractiveAuthenticationSuccessEvent`.
    - The `AuthenticationSuccessHandler` is invoked. 
    This success handler will try to redirect the user to the location when we redirect to the login page **(e.g. If you were moving to my account and got the login page, on successful login, it will redirect you to the account page.)**
5. For the fail attempt, Spring security will also perform a few important steps to make sure it **clears out** all sensitive and secure information.
    - It will clear the `SecurityContextHolder` out.
    - Call the `loginFail` method of the `RememberMeServices` service to remove cookies and other related information.
    - The `AuthenticationFailureHandler` triggers to perform any additional clean-up action.
