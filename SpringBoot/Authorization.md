# Authority  
- [Spring Security UserDetailsService 用途](https://matthung0807.blogspot.com/2019/09/spring-security-userdetailsservice.html)   
- [A Login Example](https://blog.csdn.net/pujiaolin/article/details/73928491)  
- [Role And GrantedAuthority](https://stackoverflow.com/questions/37615034/spring-security-spring-boot-how-to-set-roles-for-users/50533455)  

## Interface `GrantedAuthority` 

Grant the permissions are (normally) expressed as `String`s via the `getAuthority` method from Interface `GrantedAuthority`.   
```java
public interface GrantedAuthority extends Serializable {
	/**
	 * @return a representation 
   * of the granted authority 
   * or @code @null if the granted authority 
   *     cannot be expressed as a 
   *    @String with sufficient precision
	 */
	String getAuthority();
}
```
- These `String`s let we identify who have the *Authorization/Permission* to access specifics.

We can grant different `GrantedAuthority` (permissions) to the *users* by putting them into the _security context_.  

## Roles of Granted Authority
- [Difference between Role and GrantedAuthority in Spring Security](https://reurl.cc/o1m6Aq)

**A role is just a GrantedAuthority - a PERMISSION - a right**

The **permissions** with a naming convention that says that a role is a `GrantedAuthority` that **starts with the prefix `ROLE_`.**  
- A lot of places in spring security where the role with its `ROLE_` prefix is handled specially

*Prior to Spring security 4*, **roles were often treated the same(with prefix or without)**  
- e.g. the implementation of the `hasAuthority()` method in `SecurityExpressionRoot` which simply calls `hasRole()`).   
```java
@PreAuthorize("hasRole('ROLE_XYZ')") 
// is same as 
@PreAuthorize("hasAuthority('ROLE_XYZ')")  
```


*With Spring Security 4*, the treatment of roles is more consistent and code that deals with roles(like the `RoleVoter`, the `hasRole` expression etc.) always adds the `ROLE_` prefix for you. 
```java
// Spring Security 4
@PreAuthorize("hasRole('XYZ')")  
// is same as 
@PreAuthorize("hasAuthority('ROLE_XYZ')").
/**
  * hasAuthority('ROLE_ADMIN') 
  *  is the same as 
  * hasRole('ADMIN') 
  *  because the `ROLE_` prefix 
  *  gets added automatically. 
  */
```

### Build up `GrantedAuthority` Entity of Database 

```java
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
- the roles and operations we create in our database would be the `GrantedAuthority` representation
    > e.g. `id : ROLE_ADMIN`, `operation : OP_DELETE_ACCOUNT` ... etc.  

The **admin** role with id `ROLE_ADMIN` has the operations `OP_DELETE_ACCOUNT`, `OP_READ_ACCOUNT`,`OP_RUN_BATCH_JOB` assigned to it. 
- If *an admin* logs in the resulting security context will have the GrantedAuthorities: `ROLE_ADMIN`, `OP_DELETE_ACCOUNT`, `OP_READ_ACCOUNT`, `OP_RUN_BATCH_JOB`

The **user** role with id `ROLE_USER` has the operation `OP_READ_ACCOUNT`.  
- Which means if *a user* logs in, user will have `ROLE_USER`, `OP_READ_ACCOUNT` these GrantedAuthorities 

**When a user is authenticated, make sure that all GrantedAuthorities of all its roles and the corresponding operations are returned from the `UserDetails.getAuthorities()`.**  
  
## ROLE setup via `SimpleGrantedAuthority` 

`GrantedAuthority` and `SimpleGrantedAuthority` are provided by `springframework.security.core`    
Help us create a role of each client     
```java
// `org.springframework.security.core.authority.SimpleGrantedAuthority`
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
