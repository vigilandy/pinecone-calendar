package com.pinecone.logic;

import javax.servlet.http.HttpServletRequest;

public class CalendarLogicFactory {

  public static CalendarLogic get(HttpServletRequest request) {
    return new CalendarLogicImpl(request);
  }

}
