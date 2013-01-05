package com.pinecone.google;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.Calendar;

public class CalendarClientFactory {

  /**
   * create a new {@link Calendar} object for accessing google calendar API
   * 
   * @param credential
   * @return
   */
  public static Calendar getClient(HttpRequestInitializer credential) {
    return new Calendar.Builder(GoogleServiceUtil.HTTP_TRANSPORT,
        GoogleServiceUtil.JSON_FACTORY, credential).setApplicationName(
        GoogleServiceUtil.APPLICATION_NAME).build();
  }

}
