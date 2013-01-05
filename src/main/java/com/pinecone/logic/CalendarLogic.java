package com.pinecone.logic;

public interface CalendarLogic {

  String getAllCalendars() throws LogicException;

  String getCalendar(String calendarId) throws LogicException;

  String getCalendarEvents(String calendarId) throws LogicException;

}
