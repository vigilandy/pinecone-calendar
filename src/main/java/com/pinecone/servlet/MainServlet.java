package com.pinecone.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pinecone.google.CalendarService;
import com.pinecone.google.CalendarServiceImpl;
import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;
import com.pinecone.model.PineconeCalendar;

@WebServlet(urlPatterns = { "/" })
public class MainServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(MainServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    AuthLogic logic = AuthLogicFactory.get(request);
    if (logic.isLoggedIn()) {
      try {
        CalendarService service = new CalendarServiceImpl(logic.getCredential());
        List<PineconeCalendar> calendars = service.getCalendars();

        request.setAttribute("calendars", calendars);
        request
            .setAttribute("message", calendars.size() + " calender(s) found");

      } catch (Exception e) {
        log.error(e, e);
        request.setAttribute("message", e.toString());
      }
    }

    request.setAttribute("timestamp", new Date().toString());
    request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

  }

}
