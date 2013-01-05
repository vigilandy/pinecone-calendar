package com.pinecone.google;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleServiceUtil {

  public static final String APPLICATION_NAME = "PineconeCalendar-0.2";
  public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  public static final JsonFactory JSON_FACTORY = new JacksonFactory();
  public static final String SCOPE_USER_INFO_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
  public static final String SCOPE_USER_INFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";

}
