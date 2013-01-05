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
  public String getAllCalendars() throws GoogleServiceException {
    log.info("getting all calendars");

    try {
      com.google.api.services.calendar.Calendar.CalendarList.List listRequest = client
          .calendarList().list();
      CalendarList feed = listRequest.execute();
      if (log.isTraceEnabled()) {
        log.trace(feed.toPrettyString());
      }
      return feed.toString();
    } catch (IOException e) {
      throw new GoogleServiceException(String.format(
          "error getting all calendars: %s", e), e);
    }
  }

  @Override
  public String getCalendar(final String calendarId)
      throws GoogleServiceException {
    log.info(String.format("getting calendar '%s'", calendarId));
    try {
      return client.calendars().get(calendarId).execute().toString();
    } catch (IOException e) {
      throw new GoogleServiceException(String.format(
          "error getting calendar '%s': %s", calendarId, e), e);
    }
  }

}
