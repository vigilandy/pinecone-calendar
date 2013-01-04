package com.pinecone.google;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.model.CalendarList;

public class CalendarServiceImpl implements CalendarService {

  private static final Logger log = Logger.getLogger(CalendarServiceImpl.class);

  private final com.google.api.services.calendar.Calendar client;

  public CalendarServiceImpl(HttpRequestInitializer credential) {
    client = new com.google.api.services.calendar.Calendar.Builder(
        GoogleServiceUtil.HTTP_TRANSPORT, GoogleServiceUtil.JSON_FACTORY,
        credential).setApplicationName(GoogleServiceUtil.APPLICATION_NAME)
        .build();
  }

  @Override
  public void delete(String calendarId) throws GoogleServiceException {
    log.info(String.format("deleting calendar '%s'", calendarId));
    try {
      client.calendars().delete(calendarId).execute();
    } catch (IOException e) {
      throw new GoogleServiceException(String.format(
          "error deleting calendar '%s': %s", calendarId, e), e);
    }
  }

  @Override
  public String getOwnedCalendars() throws GoogleServiceException {
    log.info("getting owned calendars");

    try {
      com.google.api.services.calendar.Calendar.CalendarList.List listRequest = client
          .calendarList().list();
      listRequest.setMinAccessRole(AccessRole.OWNER.toString());
      CalendarList feed = listRequest.execute();
      if (log.isTraceEnabled()) {
        // log.trace(feed.toPrettyString());
      }
      return feed.toString();
    } catch (IOException e) {
      throw new GoogleServiceException(String.format(
          "error getting owned calendars: %s", e), e);
    }
  }

}
