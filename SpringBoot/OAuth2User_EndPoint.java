/** 
 * Graph of Endpoints  https://miro.medium.com/max/530/1*2NztLNRqXhMRtq73sPN4Sg.png 
 * from https://medium.com/@Junx/oauth%E5%8E%9F%E7%90%86%E8%88%87laravel-passport%E5%AF%A6%E4%BD%9C-4-7e17c0115c67
 * Session https://ithelp.ithome.com.tw/articles/10246787
 */

/************************************************************************
 * Parameter for OAuth2 Parameters                                       *
 * (e.g. Authorization Request, Access Token request/response ... etc ) *
 ************************************************************************/

public interface OAuth2ParameterNames {	
	// Used Authorization Request
	String RESPONSE_TYPE = "response_type"; 
  
   /**
    * used in Authorization Request and Access Token Request.
    */
	String CLIENT_ID = "client_id"; 
	String REDIRECT_URI = "redirect_uri";
	String CODE = "code";
	String ACCESS_TOKEN = "access_token";
	
    String ERROR = "error";
	String ERROR_DESCRIPTION = "error_description";
	String ERROR_URI = "error_uri";
    String TOKEN_TYPE = "token_type";
	String EXPIRES_IN = "expires_in";


  /**
    * used in Access Token Request 
    */
	String GRANT_TYPE = "grant_type";       // get from Authorization Response
	String CLIENT_SECRET = "client_secret"; 
	
	String CLIENT_ASSERTION_TYPE = "client_assertion_type";
	String CLIENT_ASSERTION = "client_assertion";
	String ASSERTION = "assertion";
  
	String USERNAME = "username";
	String PASSWORD = "password";
  

	/**
	 * used in Authorization Request, Authorization Response, 
             Access Token Request and Access Token Response.
	 */
	String SCOPE = "scope";

	/**
	 * used in Authorization Request and Authorization Response.
   *  state that user (e.g in which subdomain when applies for Authorization endpoint...)
	 */
	String STATE = "state";


	/**
	 * used in Access Token Request and Access Token Response.
	 */
	String REFRESH_TOKEN = "refresh_token";

	/**
	 * Non-standard parameter (used internally).
	 */
	String REGISTRATION_ID = "registration_id";

	/**
	 * Token Revocation Request.
	 */
	String TOKEN = "token";
	String TOKEN_TYPE_HINT = "token_type_hint";

}


/**************************************************
 * AuthorizationEndpoint HTTP SESSION repository   *
 **************************************************/

package org.springframework.security.oauth2.client.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;

import jdk.jfr.DataAmount;

/**
 * An implementation of an {@link AuthorizationRequestRepository} that stores
 * {@link OAuth2AuthorizationRequest} in the {@code HttpSession}.

 * @see AuthorizationRequestRepository
 * @see OAuth2AuthorizationRequest
 */
public final class HttpSessionOAuth2AuthorizationRequestRepository
		implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  /* ClassName.class.getName() get package path + class name */
	private static final String DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME = HttpSessionOAuth2AuthorizationRequestRepository.class.getName() + ".AUTHORIZATION_REQUEST";

	private final String sessionAttributeName = DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME;

	private boolean allowMultipleAuthorizationRequests;


  /* get state parameter inside Payload from HttpServletRequest 
   *     turn into Authorization Request form and get state attribute in request 
   *
   */
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Assert.notNull(request, "request cannot be null");
		String stateParameter = this.getStateParameter(request);
		if (stateParameter == null) {
			return null;
		}
    
		Map<String, OAuth2AuthorizationRequest> authorizationRequests = this.getAuthorizationRequests(request);
		return authorizationRequests.get(stateParameter);
	}


  /* getSession() : Returns the current session associated with this request, or if the request does not have a session, creates one.
   * saveAuthorizationRequest : check if state attribute exists in Authorization Request , then set object of Authorization Reuqest in httpsession contains
   *   
   *   In Servlet Contains the Server stores
   *      httpSession_1                          httpSession_2      
   *   |  name      | attribute |             |  name      | attribute |
   *   | -----------------------|             |------------------------|
   *   | session_id | 12346     |             | session_id | 14444     |
   *   | request_ob | auth_Req  |             | request_ob | auth_Req  |    
   *   |  ....      | ......    |             |  ....      | ......    |
   *   |  ....      | ......    |             |  ....      | ......    |
   */
	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		Assert.notNull(request, "request cannot be null");
		Assert.notNull(response, "response cannot be null");
    
    // parameter authorizationRequest is passed by authorization endpoint
		if (authorizationRequest == null) {
			this.removeAuthorizationRequest(request, response);
			return;
		}
    
		String state = authorizationRequest.getState();
		Assert.hasText(state, "authorizationRequest.state cannot be empty");
		
    /* Is allowMultiple Auth Requests then we need to create a Map authorizationRequests*/
    if (this.allowMultipleAuthorizationRequests) {
			Map<String, OAuth2AuthorizationRequest> authorizationRequests = this.getAuthorizationRequests(request);
			authorizationRequests.put(state, authorizationRequest);
			request.getSession().setAttribute(this.sessionAttributeName, authorizationRequests);
		}
		else {
			request.getSession().setAttribute(this.sessionAttributeName, authorizationRequest);
		}
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		Assert.notNull(request, "request cannot be null");
		String stateParameter = this.getStateParameter(request);
		if (stateParameter == null) {
			return null;
		}
		Map<String, OAuth2AuthorizationRequest> authorizationRequests = this.getAuthorizationRequests(request);
		OAuth2AuthorizationRequest originalRequest = authorizationRequests.remove(stateParameter);
		if (authorizationRequests.size() == 0) {
			request.getSession().removeAttribute(this.sessionAttributeName);
		}
		else if (authorizationRequests.size() == 1) {
			request.getSession().setAttribute(this.sessionAttributeName,
					authorizationRequests.values().iterator().next());
		}
		else {
			request.getSession().setAttribute(this.sessionAttributeName, authorizationRequests);
		}
		return originalRequest;
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
			HttpServletResponse response) {
		Assert.notNull(response, "response cannot be null");
		return this.removeAuthorizationRequest(request);
	}

	/**
	 * Gets the state parameter from the {@link HttpServletRequest}
	 * @return the state parameter or null if not found
	 */
	private String getStateParameter(HttpServletRequest request) {
		return request.getParameter(OAuth2ParameterNames.STATE);
	}

	/**
	 * Gets a non-null and mutable map of {@link OAuth2AuthorizationRequest#getState()} to
	 * an {@link OAuth2AuthorizationRequest}
	 * @param request
	 * @return a non-null and mutable map of {@link OAuth2AuthorizationRequest#getState()}
	 * to an {@link OAuth2AuthorizationRequest}.
	 */
	private Map<String, OAuth2AuthorizationRequest> getAuthorizationRequests(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Object sessionAttributeValue = (session != null) ? session.getAttribute(this.sessionAttributeName) : null;
		if (sessionAttributeValue == null) {
			return new HashMap<>();
		}
		else if (sessionAttributeValue instanceof OAuth2AuthorizationRequest) {
			OAuth2AuthorizationRequest auth2AuthorizationRequest = (OAuth2AuthorizationRequest) sessionAttributeValue;
			Map<String, OAuth2AuthorizationRequest> authorizationRequests = new HashMap<>(1);
			authorizationRequests.put(auth2AuthorizationRequest.getState(), auth2AuthorizationRequest);
			return authorizationRequests;
		}
		else if (sessionAttributeValue instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, OAuth2AuthorizationRequest> authorizationRequests = (Map<String, OAuth2AuthorizationRequest>) sessionAttributeValue;
			return authorizationRequests;
		}
		else {
			throw new IllegalStateException(
					"authorizationRequests is supposed to be a Map or OAuth2AuthorizationRequest but actually is a "
							+ sessionAttributeValue.getClass());
		}
	}

	/**
	 * Configure if multiple {@link OAuth2AuthorizationRequest}s should be stored per
	 * session. Default is false (not allow multiple {@link OAuth2AuthorizationRequest}
	 * per session).
	 * @param allowMultipleAuthorizationRequests true allows more than one
	 * {@link OAuth2AuthorizationRequest} to be stored per session.
	 * @since 5.5
	 */
	@Deprecated
	public void setAllowMultipleAuthorizationRequests(boolean allowMultipleAuthorizationRequests) {
		this.allowMultipleAuthorizationRequests = allowMultipleAuthorizationRequests;
	}
}
