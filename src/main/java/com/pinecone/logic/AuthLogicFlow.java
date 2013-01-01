package com.pinecone.logic;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
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
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.pinecone.constant.SessionAttribute;
import com.pinecone.google.GoogleServiceUtil;
import com.pinecone.google.UserService;
import com.pinecone.google.UserServiceImpl;

/**
 * uses {@link AuthorizationCodeFlow}
 */
public class AuthLogicFlow implements AuthLogic {

  private static AuthorizationCodeFlow flow;
  private static final Object FLOW_LOCK = new Object();
  private static final Logger log = Logger.getLogger(AuthLogicFlow.class);

  private static GoogleClientSecrets getClientSecrets() {
    GoogleClientSecrets clientSecrets;
    try {
      clientSecrets = GoogleClientSecrets.load(GoogleServiceUtil.JSON_FACTORY,
          AuthLogicFlow.class.getResourceAsStream(CLIENT_SECRETS_FILE));
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

  private static void initializeFlow() {

    synchronized (FLOW_LOCK) {

      if (null != flow) {
        return;
      }

      AccessMethod method = BearerToken.authorizationHeaderAccessMethod();
      GenericUrl tokenServerUrl = new GenericUrl(
          GoogleOAuthConstants.TOKEN_SERVER_URL);

      GoogleClientSecrets clientSecrets = getClientSecrets();
      ClientParametersAuthentication clientAuthentication = new ClientParametersAuthentication(
          clientSecrets.getDetails().getClientId(), clientSecrets.getDetails()
              .getClientSecret());

      AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(
          method, GoogleServiceUtil.HTTP_TRANSPORT,
          GoogleServiceUtil.JSON_FACTORY, tokenServerUrl, clientAuthentication,
          clientSecrets.getDetails().getClientId(),
          GoogleOAuthConstants.AUTHORIZATION_SERVER_URL);

      builder.setCredentialStore(getCredentialStore());
      builder.setScopes(GoogleServiceUtil.SCOPE_USER_INFO_EMAIL,
          GoogleServiceUtil.SCOPE_USER_INFO_PROFILE, CalendarScopes.CALENDAR);

      flow = builder.build();

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

  private Credential credential;
  private final HttpServletRequest request;

  AuthLogicFlow(HttpServletRequest request) {
    this.request = request;
    initializeFlow();
  }

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
    return flow.newAuthorizationUrl().setRedirectUri(getRedirectUri()).build();
  }

  @Override
  public String getUserId() {
    String userId = (String) request.getSession().getAttribute(
        SessionAttribute.USER_ID);

    if (null == userId && null != credential) {
      UserService userService = new UserServiceImpl(credential);
      userId = userService.getUserId();
      if (null != userId) {
        request.getSession().setAttribute(SessionAttribute.USER_ID, userId);
      }
    }

    return userId;
  }

  @Override
  public void handleCallback(HttpServletResponse response) {

    StringBuffer buf = request.getRequestURL();
    if (request.getQueryString() != null) {
      buf.append('?').append(request.getQueryString());
    }
    AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(
        buf.toString());

    if (log.isTraceEnabled()) {
      log.trace(logResponseUrl(responseUrl));
    }

    String code = responseUrl.getCode();
    if (responseUrl.getError() != null) {

      onAuthError(response, responseUrl);

    } else if (code == null) {

      try {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().print("Missing authorization code");
      } catch (IOException e) {
        log.error(e, e);
        onAuthError(response, responseUrl);
      }

    } else {

      String redirectUri = getRedirectUri();
      try {

        TokenResponse tokenResponse = flow.newTokenRequest(code)
            .setRedirectUri(redirectUri).execute();
        credential = flow.createAndStoreCredential(tokenResponse, null);
        getUserId();
        onAuthSuccess(response);

      } catch (IOException e) {
        log.error(e, e);
        onAuthError(response, responseUrl);
      }

    }

  }

  @Override
  public boolean isLoggedIn() {
    boolean loggedIn = false;
    String userId = getUserId();

    try {

      if (null == userId) {
        return loggedIn;
      }

      try {
        credential = null;
        credential = flow.loadCredential(userId);
      } catch (IOException e) {
        log.warn("", e);
      }

      loggedIn = null != credential;
      return loggedIn;

    } finally {

      log.debug(String.format("user id '%s' logged in=%s", userId, loggedIn));

    }

  }

  private void onAuthError(HttpServletResponse response,
      AuthorizationCodeResponseUrl responseUrl) {
    // TODO Auto-generated method stub

  }

  private void onAuthSuccess(HttpServletResponse response) {
    // TODO Auto-generated method stub
    // request.getSession().setAttribute(SessionAttribute.USER_ID,
    // credential.get)

  }
}
