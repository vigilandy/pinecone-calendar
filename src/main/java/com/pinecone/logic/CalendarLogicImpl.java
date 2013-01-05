package com.pinecone.logic;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.http.HttpRequestInitializer;
import com.pinecone.google.GoogleServiceException;
import com.pinecone.google.GoogleServiceUtil;

public class CalendarLogicImpl implements CalendarLogic {

  private final HttpServletRequest request;
  private CalendarService service = null;

  CalendarLogicImpl(HttpServletRequest request) {
    if (null == request) {
      throw new NullPointerException("request object cannot be null");
    }
    this.request = request;
  }

  @Override
  public String getAllCalendars() throws LogicException {
    try {
      return getService().getAllCalendars();
    } catch (GoogleServiceException e) {
      throw new LogicException(e);
    }
  }

  @Override
  public String getCalendar(String calendarId) throws LogicException {
    try {
      return getService().getCalendar(calendarId);
    } catch (GoogleServiceException e) {
      throw new LogicException(e);
    }
  }

  @Override
  public String getCalendarEvents(String calendarId) throws LogicException {
    getService().getEvents(calendarId);
    // TODO Auto-generated method stub
    return null;
  }

  private final com.google.api.services.calendar.Calendar client;

  private synchronized com.google.api.services.calendar.Calendar getService() {
    if (null == client) {
      AuthLogic authLogic = AuthLogicFactory.get(request);
      HttpRequestInitializer credential = authLogic.getCredential();
      client = new com.google.api.services.calendar.Calendar.Builder(
          GoogleServiceUtil.HTTP_TRANSPORT, GoogleServiceUtil.JSON_FACTORY,
          credential).setApplicationName(GoogleServiceUtil.APPLICATION_NAME)
          .build();
    }
    return client;
  }

}
