package com.pinecone.logic;

import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpRequestInitializer;

public interface AuthLogic {

  static final String CLIENT_SECRETS_FILE = "/client_secrets.json";

  HttpRequestInitializer getCredential();

  String getRedirectUrl();

  String getUserId();

  void handleCallback(HttpServletResponse response)
      throws AuthenticationErrorException;

  boolean isLoggedIn();

  void logout();

}
