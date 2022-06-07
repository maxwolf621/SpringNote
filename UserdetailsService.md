# `UserdetailsService`
[Source](https://stackoverflow.com/questions/64526372/when-should-i-override-the-configureauthenticationmanagerbuilder-auth-from-spr)  

`UserDetailsService` is used by `xxxAuthenticationProvider` for retrieving a principal(containing username, password, and other attributes from an protected Resource)  

It has one method named `loadUserByUsername()` which can be overridden to customize the process of finding the related user protected resource from server. 
- `loadUserByUsername(String username)` returns default datatype `UserDetails` which is part of `org.springframework.security.core.userdetails.User` consists of `getUsername()`, `getPassword()`, `getAuthorities()` methods are used further for spring security 

For example the relationship btw `UserDetailsService` and `DaoAuthenticationProvider` 
![](https://i.imgur.com/WnXq9Hy.png)  


## interface UserDetails
- [SourceCode Userdetails](https://reurl.cc/NAOgL9)  
- [Specification](https://reurl.cc/M0WgLL)  


```java
public interface UserDetails extends Serializable {

	Collection<? extends GrantedAuthority> getAuthorities();
	String getPassword();
	String getUsername();

	//An expired account cannot be authenticated.
	boolean isAccountNonExpired();

	/**
	 * A locked user cannot be authenticated.
	 */
	boolean isAccountNonLocked();

	/**
	 * Indicates whether the user's credentials 
     	 * (password) has expired. 
         * Expired credentials prevent authentication.
	 */
	boolean isCredentialsNonExpired();

	/**
	 * disabled user cannot be authenticated.
	 */
	boolean isEnabled();

}
```
- Interface `UserDetails` will be used by interface `UserDetailsService` (Read-Only)  


We can have implementation of `UserDetails` to have more advanced operations for authentication 
for example Build up a PRINCIPAL by `UserDetails` implementation
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

Constructor of `core.userdetails.User`
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

```java
public interface UserDetailsService {

	/**
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 * GrantedAuthority
	 */
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
```

A `UserDetailsService` implementation returns a `UserDetails` type object containing the needed `GrantedAuthorities` in Spring Security.

- ***The `UserDetailsService` would collect all roles and all operations of those roles and make them available by the method `getAuthorities()` in the returned `UserDetails` instance.*** [More Examples of Role based Authorization](https://www.codejava.net/frameworks/spring-boot/spring-boot-security-role-based-authorization-tutorial)  

- We can also customize the `UserDetails` using `org.springframework.security.core.userdetails.User` by implementing the `UserDetails` interface.

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
        Optional<User> userOptional = 
            userRepository.findByUsername(username);
        User user = 
            userOptional.orElseThrow(() -> new UsernameNotFoundException("No user " +
                        "Found with username : " + username));

        return new org.springframework.security
                .core.userdetails.User(
                    user.getUsername(), 
                    user.getPassword(),
				    user.isEnabled(), 
                    true, 
                    true,
					true, 
                    getAuthorities("USER"));
    }
    
    // Collection.singleList(T obj)
    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }
}
```

### Custom Spring Security Authentication Provider

***We also can define custom authentication provider by exposing a custom `UserDetailsService` implementation as a bean in the `websecurityconfigurerAdapter` implementation***
- For example 
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

Configure this custom authentication procedure in `WebsecurityConfigurerAapter`
```java
@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final UserDetailsPrincipalService userDetailsPrincipalService;
    /**
     * 建立一種認證方式利用UserDetailsPrincipalService提供的認證方式
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsPrincipalService userDetailsPrincipalService) throws Exception {
        auth.userDetailsService(userDetailsPrincipalService);
    }
    
    // ....
}
```
