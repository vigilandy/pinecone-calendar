package com.pinecone.logic;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events.List;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Events;
import com.pinecone.google.CalendarClientFactory;

public class CalendarLogicImpl implements CalendarLogic {

  private static final Logger log = Logger.getLogger(CalendarLogicImpl.class);
  private static final String NEXT_PAGE_TOKEN = "nextPageToken";
  private static final String TIME_MAX = "timeMax";
  private static final String TIME_MIN = "timeMin";

  private Calendar client;
  private final HttpServletRequest request;

  CalendarLogicImpl(HttpServletRequest request) {
    if (null == request) {
      throw new NullPointerException("request object cannot be null");
    }
    this.request = request;
  }

  @Override
  public String getAllCalendars() throws LogicException {
    try {
      com.google.api.services.calendar.Calendar.CalendarList.List listRequest = getClient()
          .calendarList().list();
      CalendarList feed = listRequest.execute();
      return feed.toString();
    } catch (IOException e) {
      throw new LogicException(e);
    }
  }

  @Override
  public String getCalendar(String calendarId) throws LogicException {
    try {
      com.google.api.services.calendar.model.Calendar c = getClient()
          .calendars().get(calendarId).execute();
      return c.toString();
    } catch (IOException e) {
      throw new LogicException(e);
    }
  }

  @Override
  public String getCalendarEvents(String calendarId) throws LogicException {
    try {
      List eventsRequest = getClient().events().list(calendarId);
      setEventRequestCriteria(eventsRequest);
      Events events = eventsRequest.execute();
      return events.toString();
    } catch (IOException e) {
      throw new LogicException(e);
    }
  }

  private synchronized Calendar getClient() {
    if (null == client) {
      AuthLogic authLogic = AuthLogicFactory.get(request);
      HttpRequestInitializer credential = authLogic.getCredential();
      client = CalendarClientFactory.getClient(credential);
    }
    return client;
  }

  private boolean hasParameter(String name) {
    String value = request.getParameter(name);
    return null != value && !value.isEmpty();
  }

  /**
   * check for additional criteria in request
   */
  private void setEventRequestCriteria(List eventsRequest) {

    eventsRequest.setOrderBy("startTime");
    eventsRequest.setSingleEvents(Boolean.TRUE);

    if (hasParameter(NEXT_PAGE_TOKEN)) {
      eventsRequest.setPageToken(request.getParameter(NEXT_PAGE_TOKEN));
    }

    if (hasParameter(TIME_MAX)) {
      String timeMaxParameter = request.getParameter(TIME_MAX);
      log.debug("timeMax=" + timeMaxParameter);
      DateTime timeMax = new DateTime(timeMaxParameter);
      eventsRequest.setTimeMax(timeMax);
    }

    if (hasParameter(TIME_MIN)) {
      String timeMinParameter = request.getParameter(TIME_MIN);
      log.debug("timeMin=" + timeMinParameter);
      DateTime timeMin = new DateTime(timeMinParameter);
      eventsRequest.setTimeMin(timeMin);
    }

  }

}
