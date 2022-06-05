# Spring Boot Security 


## Classes that are used Flow In SpringBoot
![image](https://user-images.githubusercontent.com/68631186/172058530-db89f590-f73d-4daa-be57-a97c9f81ef46.png)

## Authentication flow
![](https://i.imgur.com/aLKSado.png)  
1. Server creates an `UsernamePasswordAuthenticationToken` via Filter from (request containing token) the supplied client/actor request(containing username and password).
2. Pass the token to the *Authentication Manager.*
3. The *Provider Manager* will **delegate** Authentication Providers Implementations(`DaoAuthenticationProvider` for this diagram) including the *authentication token* to complete authentication procedure
4. The `DaoAuthenticationProvider` uses `UserDetailsService` implementation to get the *user information from the repository*.
5. On the successful authentication, the authentication object will contain the fully populated object including the authorities details.
6. **The returned `UsernamePasswordAuthenticationToken` will be set on the `SecurityContextHolder` by the authentication Filter.**

## The Login Workflow
![](https://i.imgur.com/Lv83VUC.png)  
1. Client fills out the credentials(e.g, password ...) on the login page.
2. On form submission, *the `UsernamePasswordAuthenticationFilter` creates a `UsernamePasswordAuthenticationToken` by extracting the `username` and `password` from the `URL` request parameters(`.../..?password=1234&username=asdf`).*
3. The `AuthenticationManager` is responsible to validate the user based on the supplied credentials 
4. If authenticated, Spring security performs several additional operations. 
    - `SessionAuthenticationStrategy` is notified for new login.  
    This handles the HTTP session and makes sure a valid session exists and handles any against session-fixation attacks.
    - Spring security stores the user authentication details in the `SecurityContextHolder`.  
    It will update the `SecurityContextHolder` with authentication details.
    - If `RememberMeServices` service is active, it will activate the `loginSuccess` method. It will publish an `InteractiveAuthenticationSuccessEvent`.
      - The `AuthenticationSuccessHandler` is invoked.  
      This success handler will try to redirect the user to the location when we redirect to the login page
    	> **(e.g. If you were moving to the account and redirected to the login page, on successful login, it will redirect you to the account page.)**
5. For the fail attempt, Spring security will also perform a few important steps to make sure it **clears out** all sensitive and secure information.
    - Clear the `SecurityContextHolder` out.
    - (optional) Call the `loginFail` method of the `RememberMeServices` service to remove cookies and other related information.
    - (optional) The `AuthenticationFailureHandler` triggers to perform any additional clean-up action.
