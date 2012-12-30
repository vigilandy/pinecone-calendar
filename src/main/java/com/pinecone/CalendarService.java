package com.pinecone;

import java.io.IOException;
import java.util.List;

import com.pinecone.model.PineconeCalendar;

public interface CalendarService {

  void delete(PineconeCalendar calendar) throws IOException;

  List<PineconeCalendar> getCalendars() throws IOException;

  PineconeCalendar insert(PineconeCalendar calendar) throws IOException;

  PineconeCalendar update(PineconeCalendar updated) throws IOException;

}
