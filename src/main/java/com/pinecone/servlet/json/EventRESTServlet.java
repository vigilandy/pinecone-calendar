package com.pinecone.servlet.json;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pinecone.logic.CalendarLogic;
import com.pinecone.logic.CalendarLogicFactory;
import com.pinecone.logic.LogicException;

@WebServlet("/rest/event")
public class EventRESTServlet extends AbstractRESTServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String action = request.getParameter(RequestParameter.ACTION);
    if (null == action) {

      writeJsonError(response, "invalid request: no action requested");

    } else if (ACTION_GET.equals(action)) {

      try {
        String calendarId = request.getParameter(RequestParameter.ID);
        if (null == calendarId) {
          writeJsonError(response, "invalid request: no calendar id");
        } else {
          CalendarLogic logic = CalendarLogicFactory.get(request);
          writeJsonString(response, logic.getCalendarEvents(calendarId));
        }
      } catch (LogicException e) {

        writeJsonError(response, e.toString());

      }

    } else {

      writeJsonError(response, String.format("invalid request: action='%s'. "
          + "For inserts, updates and deletes, use POST request", action));

    }

  }

}
