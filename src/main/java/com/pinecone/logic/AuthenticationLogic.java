package com.pinecone.logic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.common.base.Preconditions;

public class AuthenticationLogic {

  private static AuthorizationCodeFlow flow;
  private static final Object FLOW_LOCK = new Object();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final Logger log = Logger.getLogger(AuthenticationLogic.class);
  private static final NetHttpTransport TRANSPORT = new NetHttpTransport();

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
      HttpExecuteInterceptor clientAuthentication = new BasicAuthentication(
          "s6BhdRkqt3", "7Fjfp0ZBr1KtDRbnfVdmIw");
      String clientId = "s6BhdRkqt3";
      String authorizationServerEncodedUrl = "https://server.example.com/authorize";

      AuthorizationCodeFlow.Builder builder = new AuthorizationCodeFlow.Builder(
          method, TRANSPORT, JSON_FACTORY, tokenServerUrl,
          clientAuthentication, clientId, authorizationServerEncodedUrl);

      builder.setCredentialStore(new MemoryCredentialStore());

      flow = builder.build();

      // return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
      // JSON_FACTORY,
      // getClientCredential(),
      // Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
      // new AppEngineCredentialStore()).setAccessType("offline").build();

    }

  }

  private final HttpServletRequest request;

  public AuthenticationLogic(HttpServletRequest request) {
    this.request = request;
    initializeFlow();
  }

  public boolean isLoggedIn() {

    // TODO

    return false;
  }

  private static GoogleClientSecrets getClientCredential() throws IOException {
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        AuthenticationLogic.class.getResourceAsStream("/client_secrets.json"));
    Preconditions
        .checkArgument(
            !clientSecrets.getDetails().getClientId().startsWith("Enter ")
                && !clientSecrets.getDetails().getClientSecret()
                    .startsWith("Enter "),
            "Download client_secrets.json file from https://code.google.com/apis/console/?api=calendar "
                + "into calendar-appengine-sample/src/main/resources/client_secrets.json");

    return clientSecrets;
  }

}
