package com.pinecone.google;

public interface CalendarService {

  void delete(String calendarId) throws GoogleServiceException;

  /**
   * get a json string of the list of owned calendars
   */
  String getOwnedCalendars() throws GoogleServiceException;

  // String insert(String calendar) throws IOException;
  // String update(String updated) throws IOException;

}
