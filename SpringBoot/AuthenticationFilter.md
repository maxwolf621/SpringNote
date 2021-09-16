# AuthenticationFilter

To authenticate the User via UsernamePasswordAuthenticationFilter (it extends AbstractAuthenticationProcessingFilter)

```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


  public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        username = username.trim();
        
        // 
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        /**
         protected AuthenticationManager getAuthenticationManager() {
              return authenticationManager;
           }
         */
        return this.getAuthenticationManager().authenticate(authRequest);
    }
```

