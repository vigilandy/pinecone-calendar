package com.pinecone.google;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;

public class UserServiceImpl implements UserService {

  private static final Logger log = Logger.getLogger(UserServiceImpl.class);

  private final Oauth2 oauth2;

  private Userinfo userinfo;

  public UserServiceImpl(Credential credential) {
    this.oauth2 = new Oauth2.Builder(GoogleServiceUtil.HTTP_TRANSPORT,
        GoogleServiceUtil.JSON_FACTORY, credential).setApplicationName(
        GoogleServiceUtil.APPLICATION_NAME).build();
  }

  @Override
  public synchronized String getUserId() {

    if (null == userinfo) {
      try {
        userinfo = oauth2.userinfo().get().execute();
        log.debug(userinfo.toPrettyString());
      } catch (IOException e) {
        userinfo = null;
        log.error(e, e);
      }
    }

    return null == userinfo ? null : userinfo.getEmail();
  }

}
