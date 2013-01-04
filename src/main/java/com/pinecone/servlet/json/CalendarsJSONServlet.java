package com.pinecone.servlet.json;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pinecone.constant.RequestParameter;
import com.pinecone.google.CalendarService;
import com.pinecone.google.CalendarServiceImpl;
import com.pinecone.google.GoogleServiceException;
import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;

@WebServlet("/rest/calendar")
public class CalendarsJSONServlet extends AbstractJSONServlet {

  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    AuthLogic logic = AuthLogicFactory.get(request);
    if (!logic.isLoggedIn()) {
      writeJsonError(response, "user not authenticated");
      return;
    }

    /* check for parameters */
    // TODO think of a better way to make calendar requests
    String role = request.getParameter(RequestParameter.ROLE);
    String calendarId = request.getParameter(RequestParameter.CALENDAR_ID);

    CalendarService service = new CalendarServiceImpl(logic.getCredential());
    try {
      String calendarsJSON = service.getOwnedCalendars();
      writeJsonString(response, calendarsJSON);
    } catch (GoogleServiceException e) {
      writeJsonError(response, e.toString());
    }

  }

}
