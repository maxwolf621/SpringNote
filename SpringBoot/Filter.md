
# Filter 
[FLOW](https://iter01.com/583369.html)   
[Custom WebSecurity Authentication FLOW](https://www.jianshu.com/p/68885d0e1cd9)  
[Authentication Concept](https://www.jianshu.com/p/32fa221e03b7)  
[Httsecurity Methods](https://www.jianshu.com/p/2c49799479a5)  
[UsernamePasswordAuthenticationFilter](https://www.jianshu.com/p/1826627bb3a5)  

## OnePerRequestFilter
- [What is OncePerRequestFilter](https://stackoverflow.com/questions/13152946/what-is-onceperrequestfilter)   
- [SourceCode OncePerRequestFilter](https://github.com/spring-projects/spring-framework/blob/master/spring-web/src/main/java/org/springframework/web/filter/OncePerRequestFilter.java)   
- [Specification](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/filter/OncePerRequestFilter.html)  
- [`chainFilter.doFilter`](https://stackoverflow.com/questions/2057607/what-is-chain-dofilter-doing-in-filter-dofilter-method)  
- [Operation of Userdetails](https://www.mdeditor.tw/pl/gOR8/zh-tw)   
- [A login Project](https://blog.csdn.net/weixin_44516305/article/details/88868791)   

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
     * @param request current HTTP request
     * @return whether the given request 
     *         should not be filtered
     * @throws ServletException in case of errors
     */
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return false;
    }

    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }


    /**        
     *        this method is Always overridden 
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * Provides HttpServletRequest 
     * and HttpServletResponse arguments instead of the
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
This `doFilter` implementation stores a request attribute for _already filtered_, proceeding without filtering again if the attribute is already there.

- `protected abstract void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`  
**Same contract as for `doFilter`, but guaranteed to be just invoked once per request within a single request thread**.

- `protected void doFilterNestedErrorDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`  
Typically an ERROR dispatch happens after the REQUEST dispatch completes, and the filter chain starts anew.

- `protected String getAlreadyFilteredAttributeName()`  
Return the name of the request attribute that identifies that a request is already filtered.

- `protected boolean isAsyncDispatch(HttpServletRequest request)`     
a filter can be invoked in more than one thread over the course of a single request.

- `protected boolean isAsyncStarted(HttpServletRequest request)`  
Whether request processing is in asynchronous mode meaning that the response will not be committed after the current thread is exited.

- `protected boolean shouldNotFilter(HttpServletRequest request)`  
Can be overridden in subclasses for custom filtering control, returning true to avoid filtering of the given request.

- `protected boolean shouldNotFilterAsyncDispatch()`  
The dispatcher type `javax.servlet.DispatcherType.ASYNC` (Dispatcher Servlet 3.0) means **a filter can be invoked in more than one thread over the course of a single request.**

- `protected boolean shouldNotFilterErrorDispatch()`  
Whether to filter error dispatches such as when the servlet container processes and error mapped in `web.xml`.

## JWT filter 
To valid the token from client we need to customize the method `doFilterInternal` in `JwtAuthenticationFilter` extending `OncePerRequestFilter`

```java
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
