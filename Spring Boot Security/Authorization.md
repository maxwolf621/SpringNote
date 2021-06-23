###### tags: `Spring Project`  
# Login Authentication's Authority  
#### [Befroe this Chapter for Authentication & Authorization](/CrTB3w_mRm-2SFVF648fpw)  
[TOC]  
[Note for java.util.*](/mvZXnbc_RPqqtwYVdU8J1A)  
[A Login Example](https://blog.csdn.net/pujiaolin/article/details/73928491)  
[Role And GrantedAuthority](https://stackoverflow.com/questions/37615034/spring-security-spring-boot-how-to-set-roles-for-users/50533455)  
## GrantedAuthority

Great the permissions are (normally) expressed as `String`s via the `getAuthority` method from Interface `GrantedAuthority`.
These string let we identify who have the *Authorization/Permission* to access specifics.  
:::info  
We can grant different `GrantedAuthoritys` (permissions) to *users* by putting them into the security context.
By implementing `UserDetailsService` that returns a `UserDetails` implementation (containing the needed `GrantedAuthorities`). 
:::   
## Role (Name Convention)
The **permissions** with a naming convention that says that a role is a `GrantedAuthority` that **starts with the prefix `ROLE_`.**   
==A role is just a GrantedAuthority - a permission - a right.==   

- A lot of places in spring security where the role with its `ROLE_` prefix is handled specially
    > e.g. in the `RoleVoter`, where the `ROLE_` prefix is used as a default. This allows you to provide the role names without the `ROLE_ `prefix.  
- *Prior to Spring security 4*, this special handling of roles has not been followed very consistently and authorities and **roles were often treated the same(with prefix or without)**  
    > e.g. the implementation of the `hasAuthority()` method in `SecurityExpressionRoot` - which simply calls `hasRole()`).   

- *With Spring Security 4*, the treatment of roles is more consistent and code that deals with roles(like the RoleVoter, the hasRole expression etc.) always adds the `ROLE_` prefix for you.
    > So `hasAuthority('ROLE_ADMIN')` means the the same as `hasRole('ADMIN')` because the `ROLE_` prefix gets added automatically. 

#### Difference of Annotation Role btw SpringSecurity 3/4
```java=
// Spring Security 3
@PreAuthorize("hasRole('ROLE_XYZ')") // is same as 
@PreAuthorize("hasAuthority('ROLE_XYZ')")  

// Spring Security 4
@PreAuthorize("hasRole('XYZ')")  // is same as 
@PreAuthorize("hasAuthority('ROLE_XYZ')").
```

### Example To build up GrantedAuthority Entity  
The `GrantedAuthorities` for the roles have the prefix `ROLE_` and the operations have the prefix `OP_`.     
For example  
```java=
/* Role */
@Entity
class Role implements GrantedAuthority {
    @Id
    private String id;
    
    // Roles can have multiple operations
    @ManyToMany
    private final List<Operation> allowedOperations = new ArrayList<>();
    
    @Override
    public String getAuthority() {
        return id;
    }
    
    public Collection<GrantedAuthority> getAllowedOperations() {
        return allowedOperations;
    }
}

/* Model Class */
@Entity
class User {
    @Id
    private String id;

    @ManyToMany
    private final List<Role> roles = new ArrayList<>();

    public Collection<Role> getRoles() {
        return roles;
    }
}

/* Operation */
@Entity
class Operation implements GrantedAuthority {
    @Id
    private String id;

    @Override
    public String getAuthority() {
        return id;
    }
}
```
- the `ids` of the roles and operations we create in our database would be the `GrantedAuthority` representation
    > e.g. `ROLE_ADMIN`, `OP_DELETE_ACCOUNT` ... etc.  
:::danger  
When a user is authenticated, make sure that all _GrantedAuthorities_ of all its roles and the corresponding operations are returned from the `UserDetails.getAuthorities()` method.
:::  

> The *admin role* with id `ROLE_ADMIN` has the operations
>> `OP_DELETE_ACCOUNT`, `OP_READ_ACCOUNT`, `OP_RUN_BATCH_JOB` assigned to it. 
>
> The *user role* with id `ROLE_USER` has the operation
>> `OP_READ_ACCOUNT`.  
:::info  
In short   
- If *an admin* logs in the resulting security context will have the GrantedAuthorities: `ROLE_ADMIN`, `OP_DELETE_ACCOUNT`, `OP_READ_ACCOUNT`, `OP_RUN_BATCH_JOB`
- If *a user* logs it, it will have: `ROLE_USER`, `OP_READ_ACCOUNT`  
:::  

==***The `UserDetailsService` would take care to collect all roles and all operations of those roles and make them available by the method `getAuthorities()` in the returned UserDetails instance.***==  
[More Examples of Role based Authorization](https://www.codejava.net/frameworks/spring-boot/spring-boot-security-role-based-authorization-tutorial)  

## ROLE setup via `org.springframework.security.core.authority.SimpleGrantedAuthority`
We need to use `GrantedAuthority` and `SimpleGrantedAuthority` to authenticate our needed User information (and what role is the needed User )  
[Reference](https://www.cnblogs.com/longfurcat/p/9417422.html)  


#### Interface GrantedAuthority 
```java=
public interface GrantedAuthority extends Serializable {
	/**
	 * @return a representation of the granted authority (or @code null) 
	 *	if the granted authority cannot be 
	 *      expressed as a @String with sufficient precision).
	 */
	String getAuthority();
}
```

Class SimpleGrantedAuthority ( Create A Role and Store in the data base ) 
```java=
public final class SimpleGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 
        SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final String role;
	
	// Create A Role (USER, ADMIN ... etc )
	public SimpleGrantedAuthority(String role) {
		Assert.hasText(role, "A granted authority textual \
                            representation is required");
		this.role = role;
	}
	
	@Override
	public String getAuthority() {
		return this.role;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SimpleGrantedAuthority) {
			return this.role.equals(((SimpleGrantedAuthority) obj).role);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return this.role.hashCode();
    }
	@Override
	public String toString() {
		return this.role;
	}
}
```

### `org.Springframework.security.core.userdetails`  
[UserDetails SourceCode](https://github.com/spring-projects/spring-security/blob/master/core/src/main/java/org/springframework/security/core/userdetails/UserDetails.java)  
[Specification](https://docs.spring.io/spring-security/site/docs/4.2.20.RELEASE/apidocs/org/springframework/security/core/userdetails/UserDetails.html)  

As the above we said before the `UserDetailsService` would take care to 
- **collect all roles** 
- and **all operations of those roles**

Customize userDetails by implementing UserDetails
```java=
public class UserPrincipal implements UserDetails {
    // Login User
    private User user;
    // for GrantedAuthority
    private List<String> roleCodes;
    // Constructor
    UserPrincipal(User user,List<String> roleCodes){
        this.user = user;
        this.roleCodes = roleCodes;
    }

   /**
    * GrantedAuthority setup for ROLES
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleCodes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    //..
}
```

### `org.springframework.security.core.userdetails.User`
[Specification](https://docs.spring.io/spring-security/site/docs/4.2.20.RELEASE/apidocs/org/springframework/security/core/userdetails/User.html)

[Reference](https://stackoverflow.com/questions/19525380/difference-between-role-and-grantedauthority-in-spring-security)
> Why uses USER instead of Userdetails ? 

#### Constructor of `core.userdetails.User`
Construct the User with the details required by `DaoAuthenticationProvider`.
```java=
public User(//the username presented to DaoAuthenticationProvider
            String username,
            //the password that should be 
            //    presented to the DaoAuthenticationProvider
            String password,
            //set to true if the user is enabled
            boolean enabled,
            //set to true if the account has not expired
            boolean accountNonExpired,
            //set to true if the credentials have not expired
            boolean credentialsNonExpired,
            // set to true if the account is not locked
            boolean accountNonLocked,
            // the authorities that should be granted to the caller 
            //    if they presented the correct username and password and the user is enabled. 
            //        Not null.
            Collection<? extends GrantedAuthority> authorities)
```

## Customized Class that extends `WebSecurityConfigurerAdapter` with `core.userdetails.User`

[WebSecurityConfigurerAdapter_Methods](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html)  
[Example](https://blog.csdn.net/weixin_44516305/article/details/88868791)  

To extend `WebSecurityConfigurerAdapter` to (often) override two methods  
```java=
configure(AuthenticationManagerBuilder auth)
configure(HttpSecurity http)
```
> `AuthenticationManagerBuilder` 
> : - Authentication for Role (register the User)
> : - Set Up In-Memory User
>
> `HttpSecurity`
> : The Authorization of each accessing web page for who/which Role needs to get the Authentication


```java=
// Configure the DauAuthenticationProvider
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); 
}

/**
 * @return Provider who can support us Au thentication
 */
@Bean
public DaoAuthenticationProvider authenticationProvider() 
{
    DaoAuthenticationProvider authenticationProvider = 
        new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(customUserService); 
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
}

@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Give Authentication to Authentication Manager
    auth.authenticationProvider(authenticationProvider()); 
}
```

## UserDetailsService


```java=
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
