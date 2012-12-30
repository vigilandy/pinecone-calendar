package com.pinecone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.pinecone.model.PineconeCalendar;

public class CalendarServiceImpl implements CalendarService {

  private static com.google.api.services.calendar.Calendar loadCalendarClient(
      String userId) {
    // TODO Auto-generated method stub
    return null;
  }

  private final com.google.api.services.calendar.Calendar client;

  public CalendarServiceImpl(String userId) throws IOException {
    client = loadCalendarClient(userId);
  }

  @Override
  public void delete(PineconeCalendar calendar) throws IOException {
    client.calendars().delete(calendar.getId()).execute();
  }

  @Override
  public List<PineconeCalendar> getCalendars() throws IOException {
    com.google.api.services.calendar.Calendar.CalendarList.List listRequest = client
        .calendarList().list();
    listRequest.setFields("items(id,summary)");
    CalendarList feed = listRequest.execute();
    List<PineconeCalendar> result = new ArrayList<PineconeCalendar>();
    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        result.add(new PineconeCalendar(entry.getId(), entry.getSummary()));
      }
    }
    return result;
  }

  @Override
  public PineconeCalendar insert(PineconeCalendar calendar) throws IOException {
    Calendar newCalendar = new Calendar().setSummary(calendar.getTitle());
    Calendar responseEntry = client.calendars().insert(newCalendar).execute();
    PineconeCalendar result = new PineconeCalendar();
    result.setTitle(responseEntry.getSummary());
    result.setId(responseEntry.getId());
    return result;
  }

  @Override
  public PineconeCalendar update(PineconeCalendar updated) throws IOException {
    Calendar entry = new Calendar();
    entry.setSummary(updated.getTitle());
    String id = updated.getId();
    Calendar responseEntry = client.calendars().patch(id, entry).execute();
    return new PineconeCalendar(id, responseEntry.getSummary());
  }

}
