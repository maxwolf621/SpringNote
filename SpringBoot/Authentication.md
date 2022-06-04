# Authentication
- [Type of Authentication](https://blog.csdn.net/gdp12315_gu/article/details/79905424)   
- [Definition of User, Principal, Subject](https://matthung0807.blogspot.com/2018/03/spring-security-principal.html)    
- [Authentication in HTTP format ](https://blog.csdn.net/kiwi_coder/article/details/28677651)   
- [Spring Security Authentication Providers](https://www.javadevjournal.com/spring-security/spring-security-authentication-providers/)    

## Definitions of Authentication and Authorization
Authentication : prove who are you

Authorization : give a permission (the things you can do, things you cant do). In spring security we set up our authorization with GrantedAuthority and Role
## Definitions in Authentication 

- Subject
  - **In a security context, a subject is any entity that requests access to an object**.
  - (e.g. when you log onto an application you are the subject and the application is the object)
  - (In reality world) when someone knocks on your door the visitor is the subject requesting access and your home is the object access is requested of.
- Principal
  - **A subset of subject that is represented by an account, role or other unique identifier.** It may represent human users, automation, applications, connections, etc...
  - **When we get to the level of implementation details(interface `UserDetails`), principals are the unique keys we use in access control lists.**  
- User 
  - **A subset of principal usually referring to a human operator.**  
  - When you need to make the distinction between the broad class of things that are principals and the subset of these that are interactive operators driving transactions in a non-deterministic fashion, "user" is the right word.  

## Authentication Architecture 
![](https://i.imgur.com/XSbxJTh.png)

### SecurityContextHolder

SecurityContextHolder creates ThreadLocal to store current Spring Security's Context (containing any related with Spring Security) for Current thread

### SecurityContext
`SecurityContext` 持有的是代表當前使用者相關Authentication資訊的Reference 

### Authentication 

Spring Security uses an `Authentication` instance to apply current user's information
```console
details
Credentials (password ... etc)
principal
authorities of the principal 
```
**Authentication 物件不需要我們自己去建立，在與系統互動的過程中，Spring Security 會自動為我們建立相應的 Authentication Object (via `UserNamePasswordAuthenticationToken`)，然後賦值給當前的 SecurityContext。**

- **Many of the authentication providers will create a `UserDetails` object as the principal.**  

```java
public interface Authentication extends Principal, Serializable {    
    /**
	 - Set by an @codeAuthenticationManager to indicate the authorities that the principal has been granted. 
	 - Note that classes should not rely on this value as being valid unless it has been set by a trusted @codeAuthenticationManager.
	 - @return the authorities granted to the principal, or an empty collection if the token has not been authenticated. Never null.
	 */
	Collection<? extends GrantedAuthority> getAuthorities();

	/**
	 * The credentials that prove the principal is correct. 
	 * This is usually a password,
	 * but could be anything relevant to the <code>AuthenticationManager</code>. 
	 * Callers are expected to populate the credentials.
	 */
	Object getCredentials();

	/**
	 * Stores additional details about the authentication request. 
	 * {@Link Used in the Oauth2User}
	 * These might be an IP address, certificate serial number etc.
	 * @return additional details about the authentication request, 
	 * or null if not used
	 */
	Object getDetails();

	/**
	 - The identity of the principal being authenticated. 
	 - In the case of an authentication request with username and password, this would be the username. 
	 - Callers are expected to populate the principal for an authentication request.
	 - The <tt>AuthenticationManager</tt> implementation will often return an
	   `Authentication` containing richer information as the principal for use by the application. 
	 - Many of the authentication providers will create a @code UserDetails object as the principal.
	 */
	Object getPrincipal();


	/**
	 - Used to indicate to {@code AbstractSecurityInterceptor} whether it should present the authentication token to the <code>AuthenticationManager</code>. 
	 - Typically an <code>AuthenticationManager</code> (or, more often, one of its
	   <code>AuthenticationProvider</code>s) will return an immutable authentication token
	 - after successful authentication, in which case that token can safely return
	   <code>true</code> to this method. 
	 - Returning <code>true</code> will improve performance, as calling the <code>AuthenticationManager</code> for every request will no longer be necessary.
	 - For security reasons, implementations of this interface should be very careful about returning <code>true</code> from this method unless they are either
	   immutable, or have some way of ensuring the properties have not been changed since original creation.
	 - @return true if the token has been authenticated and the <code>AbstractSecurityInterceptor</code> does not need to present the token to the
	   <code>AuthenticationManager</code> again for re-authentication.
	 */
	boolean isAuthenticated();

	/**
	 - Implementations should always allow this method to be called with a
	   <code>false</code> parameter, as this is used by various classes to specify the
	   authentication token should not be trusted. 
	 - If an implementation wishes to reject an invocation with a <code>true</code> parameter 
	   (which would indicate the authentication token is trusted - a potential security risk) the implementation
	   should throw an {@link IllegalArgumentException}.
	 - @throws IllegalArgumentException if an attempt to make the authentication token
	   trusted (by passing <code>true</code> as the argument) is rejected due to the
	 */
	void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
```

To get a **Authenticated User** from Spring Security of the Current Thread using `.getPrincipal()`
```java
Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
if (principal instanceof UserDetails) {
    String username = ((UserDetails)principal).getUsername();
} else {
    String username = principal.toString();
}
```
- `.getContext()` : get Current Security Context  
- `.getAuthentication()`: get the currently **authenticated principal**, or an authentication **request token**


## Authentication Manager and Provider
[GoodeReference](https://blog.csdn.net/weixin_42281735/article/details/105289155)

#### Relationship of `AuthenticationManager`, `ProviderManager` and `AuthenticationProviders`  

![](https://i.imgur.com/e5L19Cv.png)  
- Spring Security checks the Authentication of Client's Request using `ProviderManager` that implements `AuthenticationManager`.  
- `ProviderManager` then delegates* the numbers of `AuthenticationProvider` implementations to do the actual authentication procedure

![](https://i.imgur.com/wddCsgT.png)  

## The whole Authentication Logic

![](https://i.imgur.com/IFhrSwP.png)
![](https://i.imgur.com/If4HFFG.png)


## Authentication Provider's Implementations

Spring Security provides a variety of options for performing authentication.  
- an Authentication request is processed by an AuthenticationProvider and a fully authenticated object with full credentials is returned. 

More custom scenarios will still need to access the full Authentication request to be able to perform the authentication process.   
For example, when authenticating against some external, third party service (such as Crowd) - both the username and the password from the authentication request will be necessary.  
To deal with such we [customize Authentication Provider](https://www.baeldung.com/spring-security-authentication-provider)


```java
public interface AuthenticationProvider {
    Authentication authenticate(
        Authentication authentication) throws AuthenticationException;

    boolean supports(Class<?> authentication);
}
```
- `authenticate` encapsulates `userId` and `password` from Client's request as an instance of `Authentication`

`AuthenticationProvider` implementations are responsible to perform a specific authentication.

## DaoAuthenticationProvider

There are many different `AuthenticationProvider` implementations for a specific authentication *(respecting with the client's request)*
- e.g `DaoAuthenticationProvider` which retrieves the user details from a simple, read-only user DAO via the `UserDetailsService`. This User Details Service only has access to the username in order to retrieve the full user entity(This is enough for most scenarios).
    ```java
    public interface UserDetailsService {
        // var1 as login id
        UserDetails loadUserByUsername(
            String var1) throws UsernameNotFoundException;
    }
    ```
- `loadUserByUsername` using `UserDetails` and `JpaRepository` to query DataBase to compare with information from client's request
- `UserDetailsService`、`UserDetails` and `UserDetailsManager` allow us to be implemented by customized class (like ORM, Hibernate framework ... )


[SourceCode `DaoAuthenticationProvider`](https://reurl.cc/M0WgYK)

```java
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	/**
	 * The plaintext password 
     * when the user is not found.
	 */
	private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
	/**
	 * This is necessary, because some
	 * {@link PasswordEncoder} implementations 
     * will short circuit if the password is not
	 * in a valid format.
	 */
	private volatile String userNotFoundEncodedPassword;

    private PasswordEncoder passwordEncoder;
    // xxxDetailsService
    private UserDetailsService userDetailsService;
    private UserDetailsPasswordService userDetailsPasswordService;

    // Constructor
	public DaoAuthenticationProvider() {
		setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
	}

	...
    
	@Override
	protected void doAfterPropertiesSet() {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	@Override
	protected final UserDetails retrieveUser(
        String username, 
        UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		prepareTimingAttackProtection();
		try {
			UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);

			if (loadedUser == null) {
				throw new InternalAuthenticationServiceException(
						"UserDetailsService returned null, which is an interface contract violation");
			}
			return loadedUser;
		}
		catch (UsernameNotFoundException ex) {
			mitigateAgainstTimingAttack(authentication);
			throw ex;
		}
		catch (InternalAuthenticationServiceException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
		}
	}

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		boolean upgradeEncoding = this.userDetailsPasswordService != null
				&& this.passwordEncoder.upgradeEncoding(user.getPassword());
		if (upgradeEncoding) {
			String presentedPassword = authentication.getCredentials().toString();
			String newPassword = this.passwordEncoder.encode(presentedPassword);
			user = this.userDetailsPasswordService.updatePassword(user, newPassword);
		}
		return super.createSuccessAuthentication(principal, authentication, user);
	}

	private void prepareTimingAttackProtection() {
		if (this.userNotFoundEncodedPassword == null) {
			this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
		}
	}

	private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
		if (authentication.getCredentials() != null) {
			String presentedPassword = authentication.getCredentials().toString();
			this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
		}
	}

	
    /** setter and getter **/

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
		this.passwordEncoder = passwordEncoder;
		this.userNotFoundEncodedPassword = null;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return this.passwordEncoder;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	public void setUserDetailsPasswordService(UserDetailsPasswordService userDetailsPasswordService) {
		this.userDetailsPasswordService = userDetailsPasswordService;
	}

}
```
Method `createSuccessAuthentication`
```java
protected Authentication createSuccessAuthentication(Object principal,
    Authentication authentication, UserDetails user) {
  UsernamePasswordAuthenticationToken result = 
      new UsernamePasswordAuthenticationToken(
          principal, authentication.getCredentials(),
          authoritiesMapper.mapAuthorities(user.getAuthorities()));
  result.setDetails(authentication.getDetails());

  return result;
```
- Ensure we return the original credentials the user supplied,subsequent attempts are successful even with encoded passwords.
- Ensure we return the original `.getDetails()`, so that future authentication events after cache expiry contain the details

## Customize AuthenticationProvider 

We can define **custom authentication** by exposing a custom `UserDetailsService` as a bean). 
Customized User Service can load principal based on our data model.  
- For example
```java
@Service
public class CustomUserDetailService implements UserDetailsService {

    // Repository or Service 
    @Autowired
    UserRepository userRepository;

    @Override
    // String email as our login iD
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final UserEntity customer = userRepository.findByEmail(email);
        if (customer == null) {
            throw new UsernameNotFoundException(email);
        }
        // fetch from database using UserDetails
        UserDetails user = User.withUsername(
            customer.getEmail())
                    .password(customer.getPassword())
                    .authorities("USER").build();
        return user;
    }
}
```
- we need `JPA/CRUD Repository` to help `UserDetails` fetches user information from data base for comparing validation of client's request

## interface UserDetails
- [SourceCode Userdetails](https://reurl.cc/NAOgL9)  
- [Specification](https://reurl.cc/M0WgLL)  

To build up a PRINCIPAL by `UserDetails` implementation
```java
public class UserPrincipal implements UserDetails {
    // Login User (password, name, ... etc ) 
    private User user;
    // for GrantedAuthority 
    private List<String> hasRoles;
    
    // Constructor
    UserPrincipal(User user,List<String> roles){
        this.user = user;
        this.hasRoles = roles;
    }

   /**
    * GrantedAuthority setup for ROLES
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return hasRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    //..
}
```

## userdetails.User
- [Specification](https://reurl.cc/6ZXMxb)

This Class constructs the User with the details required by `DaoAuthenticationProvider`.

#### Constructor of `core.userdetails.User`
```java
public User(String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities)
```
- `username` presented to `DaoAuthenticationProvider`
- `password` that should be presented to the `DaoAuthenticationProvider`
- `enabled` set to `true` if the user is enabled
- `accountNonExpired` set to `true` if the account has not expired
- `credentialsNonExpired` set to `true` if the credentials have not expired
- `authorities` the authorities that should be granted to the caller if they presented the correct username and password and the user is enabled. Not `null`.

## UserDetailsService

A `UserDetailsService` implementations returns a `UserDetails` implementation containing the needed `GrantedAuthorities` in Spring Security.

- ***The `UserDetailsService` would collect all roles and all operations of those roles and make them available by the method `getAuthorities()` in the returned `UserDetails` instance.*** [More Examples of Role based Authorization](https://www.codejava.net/frameworks/spring-boot/spring-boot-security-role-based-authorization-tutorial)  

```java
import com.example.springredditclone.model.User;
import com.example.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("No user " +
                        "Found with username : " + username));

        return new org.springframework.security
                .core.userdetails.User(user.getUsername(), user.getPassword(),
						user.isEnabled(), true, true,
						true, getAuthorities("USER"));
    }
    
    // Collection.singleList(T obj)
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }
}
```

## Security Builder
[Good Reference](https://medium.com/@yovan/spring-security-configuration-architecture-c9694435330a)  
[Reference](https://www.javadevjournal.com/spring-security/spring-security-login/)  

Interface SecurityBuilder help us to build up `SecurityConfigurer` Set.
![](https://i.imgur.com/S8nhgsZ.png)  



A SecurityConfigurer set are filters for series of authentication procession (e.g below diagram)
![](https://i.imgur.com/Y1VV0zM.png)  

There are THREE Security Builders implementation that are provided by Spring Security generated by `WebSecurityConfigurerAdapter`
![](https://i.imgur.com/nLBXbID.png)   
1. `WebSecurity` & `HttpSecurity`
    >Each `WebSecurityConfigurerAdapter` implementation will create different `WebSecurity` or `HttpSecurity` as the Filter (to form a Filter Chain) 
2. `AuthenticationManagerBuilder` (How to Authenticate)
    - Spring Security provides some configuration helpers to quickly get common authentication manager features set up in your application. **The most commonly used helper is the `AuthenticationManagerBuilder`, which is great for setting up in-memory, JDBC, or LDAP user details or for adding a custom `UserDetailsService`**  

**We could have various `WebSecurityConfigurerAdapter` implementations to security multiple layers web points**

## WebSecurity and HttpSecurity
- [Class `HttpSecurity`](https://reurl.cc/6ZXMRO)  
- [Examples](https://blog.csdn.net/weixin_44516305/article/details/88868791)

`WebSecurityConfigurerAdapter` provides a set of methods to enable specific web security configuration via different Security Builders (e.g `HttpSecurity` , `WebSecurity` ... etc ...)  

For example
```java
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
      * configure() method is used to configure distinct security points 
      *  for our application 
      *  (e.g. secure and non-secure urls, success handlers etc.).
      */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            //Login and register page to be accessible without any required(Everyone can access theses two pages).
            .antMatchers("/login", "/register").permitAll()
            //Allowing only logged is customer to access URLs matching with pattern `/account/**`.
            //     Looking for a certain role before allowing the user to access the URL.
            .antMatchers("/account/**").access("hasRole('ROLE_ADMIN')")
            .and()
            .formLogin(form - > 
	    	form.loginPage("/login")
                	// A successful authentication, then redirect the user to
                	.defaultSuccessUrl("/home")
                	// if login fails, then redirect the user to 
                	.failureUrl("/login?error=true")
            );
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers("/resources/**", "/static/**");
    }

    /* How Our Authentication authenticates the users */
  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() 
  {
      DaoAuthenticationProvider authenticationProvider = 
          new DaoAuthenticationProvider();
      authenticationProvider.setUserDetailsService(customUserService); 
      authenticationProvider.setPasswordEncoder(passwordEncoder());
      return authenticationProvider;
  }

  // Build Up a Authentication Provider 
  // to authenticate the client
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Configure Authentication Procedure
    // using daoAuthenticationProvider
    // to authenticate the User accounts
    auth.authenticationProvider(daoAuthenticationProvider()); 
  }
}
```
- When clients sent requests, these will be intercepted by `FilterSecurityInterceptor` and filtered by various `WebSecurityConfigurerAdapter` implementations.
  > Once the Request's URL that requires the authentication Spring會從`SecurityContextHolder`取得該Client的`Authentication` reference. 判斷是否已經認證過，決定該Client能不能Access。


