package com.pinecone.servlet.json;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pinecone.logic.CalendarLogic;
import com.pinecone.logic.CalendarLogicFactory;
import com.pinecone.logic.LogicException;

@WebServlet("/rest/calendar")
public class CalendarRESTServlet extends AbstractRESTServlet {

  private static final Logger log = Logger.getLogger(CalendarRESTServlet.class);
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String action = request.getParameter(RequestParameter.ACTION);
    if (null == action) {

      writeJsonError(response, "invalid request: no action requested");

    } else if (ACTION_GET.equals(action)) {

      try {
        CalendarLogic logic = CalendarLogicFactory.get(request);
        String calendarId = request.getParameter(RequestParameter.ID);
        if (null == calendarId || "all".equals(calendarId)) {
          writeJsonString(response, logic.getAllCalendars());
        } else {
          writeJsonString(response, logic.getCalendar(calendarId));
        }
      } catch (LogicException e) {

        log.error(e, e);
        writeJsonError(response, e.toString());

      }

    } else {

      writeJsonError(response, String.format("invalid request: action='%s'. "
          + "For inserts, updates and deletes, use POST request", action));

    }

  }

}
