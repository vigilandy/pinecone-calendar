package com.pinecone.logic;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.pinecone.constant.SessionAttribute;
import com.pinecone.google.GoogleServiceUtil;
import com.pinecone.google.UserService;
import com.pinecone.google.UserServiceImpl;

public class AuthLogicImpl implements AuthLogic {

  private static GoogleClientSecrets clientSecrets;
  private static CredentialStore credentialStore;
  private static final Lock lock = new ReentrantLock();
  private static final Logger log = Logger.getLogger(AuthLogicImpl.class);

  private static HttpExecuteInterceptor getClientAuthentication() {
    return new ClientParametersAuthentication(clientSecrets.getDetails()
        .getClientId(), clientSecrets.getDetails().getClientSecret());
  }

  private static GoogleClientSecrets getClientSecrets() {
    GoogleClientSecrets clientSecrets;
    try {
      clientSecrets = GoogleClientSecrets.load(GoogleServiceUtil.JSON_FACTORY,
          AuthLogicImpl.class.getResourceAsStream(CLIENT_SECRETS_FILE));
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to load client secrets from "
          + CLIENT_SECRETS_FILE, e);
    }

    if (log.isTraceEnabled()) {
      log.trace(String.format("client id '%s', client secret '%s'",
          clientSecrets.getDetails().getClientId(), clientSecrets.getDetails()
              .getClientSecret()));
    }

    boolean validClientId = !clientSecrets.getDetails().getClientId()
        .startsWith("Enter ");
    boolean validClientSecret = !clientSecrets.getDetails().getClientSecret()
        .startsWith("Enter ");

    if (!validClientId || !validClientSecret) {
      throw new IllegalArgumentException(
          "Download client_secrets.json file from "
              + "https://code.google.com/apis/console/?api=calendar into "
              + CLIENT_SECRETS_FILE);
    }

    return clientSecrets;
  }

  private static CredentialStore getCredentialStore() {
    return new MemoryCredentialStore();
  }

  private static String[] getScopes() {
    return new String[] { GoogleServiceUtil.SCOPE_USER_INFO_EMAIL,
        GoogleServiceUtil.SCOPE_USER_INFO_PROFILE, CalendarScopes.CALENDAR };
  }

  private static void initialize() {
    lock.lock();
    try {
      if (null == clientSecrets) {
        log.trace("initializing client secrets");
        clientSecrets = getClientSecrets();
      }
      if (null == credentialStore) {
        log.trace("initializing credential store");
        credentialStore = getCredentialStore();
      }
    } finally {
      lock.unlock();
    }
  }

  private static String logResponseUrl(AuthorizationCodeResponseUrl responseUrl) {
    StringBuilder sb = new StringBuilder("response URL: "
        + responseUrl.toString());

    sb.append(String.format("entry count=%s", responseUrl.size()));
    for (Entry<String, Object> entry : responseUrl.entrySet()) {
      sb.append(String.format(" [%s] %s", entry.getKey(), entry.getValue()));
    }

    return sb.toString();
  }

  private static Credential newCredential() {

    AccessMethod method = BearerToken.authorizationHeaderAccessMethod();
    Credential.Builder builder = new Credential.Builder(method);
    builder.setTransport(GoogleServiceUtil.HTTP_TRANSPORT);
    builder.setJsonFactory(GoogleServiceUtil.JSON_FACTORY);
    builder.setTokenServerEncodedUrl(GoogleOAuthConstants.TOKEN_SERVER_URL);
    builder.setClientAuthentication(getClientAuthentication());
    return builder.build();

  }

  private Credential credential;

  private final HttpServletRequest request;

  AuthLogicImpl(HttpServletRequest request) {
    initialize();
    if (null == request) {
      throw new NullPointerException("request object cannot be null");
    }
    this.request = request;
  }

  @Override
  public HttpRequestInitializer getCredential() {
    return credential;
  }

  private String getRedirectUri() {
    GenericUrl url = new GenericUrl(request.getRequestURL().toString());
    url.setRawPath(request.getContextPath() + "/oauth2callback");
    return url.build();
  }

  @Override
  public String getRedirectUrl() {
    String clientId = clientSecrets.getDetails().getClientId();
    AuthorizationCodeRequestUrl authUrl = new AuthorizationCodeRequestUrl(
        GoogleOAuthConstants.AUTHORIZATION_SERVER_URL, clientId)
        .setScopes(getScopes());
    String redirectUrl = authUrl.setRedirectUri(getRedirectUri()).build();
    log.trace("redirect url: " + redirectUrl);
    return redirectUrl;
  }

  @Override
  public String getUserId() {
    return getUserIdFromSession();
  }

  private String getUserIdFromCredentials() {
    if (null == credential) {
      throw new IllegalArgumentException("no credentials set");
    }

    UserService userService = new UserServiceImpl(credential);
    return userService.getUserId();
  }

  private String getUserIdFromSession() {
    return (String) request.getSession().getAttribute(SessionAttribute.USER_ID);
  }

  @Override
  public void handleCallback(HttpServletResponse response)
      throws AuthenticationErrorException {

    StringBuffer buf = request.getRequestURL();
    if (request.getQueryString() != null) {
      buf.append('?').append(request.getQueryString());
    }
    AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(
        buf.toString());

    if (log.isTraceEnabled()) {
      log.trace(logResponseUrl(responseUrl));
    }

    try {

      String code = responseUrl.getCode();
      if (responseUrl.getError() != null) {

        throw new AuthenticationErrorException(responseUrl.getError());

      } else if (code == null) {

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().print("Missing authorization code");
        return;

      }

      /* generate token request */
      AuthorizationCodeTokenRequest tokenRequest;
      {
        tokenRequest = new AuthorizationCodeTokenRequest(
            GoogleServiceUtil.HTTP_TRANSPORT, GoogleServiceUtil.JSON_FACTORY,
            new GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL), code);
        tokenRequest.setClientAuthentication(getClientAuthentication());
        tokenRequest.setScopes(getScopes());
        tokenRequest.setRedirectUri(getRedirectUri());
      }

      /* execute request and get response */
      TokenResponse tokenResponse = tokenRequest.execute();

      /* create new credential */
      credential = newCredential();
      credential.setFromTokenResponse(tokenResponse);

      String userId = getUserIdFromCredentials();
      credentialStore.store(userId, credential);

      log.debug(String.format("setting session user id '%s'", userId));
      request.getSession().setAttribute(SessionAttribute.USER_ID, userId);

    } catch (IOException e) {
      throw new AuthenticationErrorException(e);
    }

  }

  @Override
  public boolean isLoggedIn() {
    String userId = getUserIdFromSession();
    if (null == userId) {
      return false;
    }

    try {
      credential = newCredential();
      if (credentialStore.load(userId, credential)) {
        return true;
      }
    } catch (IOException e) {
      log.error(e, e);
    }

    credential = null;
    return false;
  }

  @Override
  public void logout() {

    String userId = getUserIdFromSession();
    if (null == userId) {
      /* user already logged out */
      return;
    }

    /* reset session */
    request.getSession().removeAttribute(SessionAttribute.USER_ID);
    request.getSession().removeAttribute(SessionAttribute.LOGGED_IN);

    /* remove credentials */
    try {
      credentialStore.delete(userId, credential);
    } catch (IOException e) {
      log.error(e, e);
    } finally {
      credential = null;
    }

  }

}
