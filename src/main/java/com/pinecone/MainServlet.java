package com.pinecone;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = { "/" })
public class MainServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(MainServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    log.info("get request for " + request.getRequestURI());

    try {
      // CalendarService service = new CalendarServiceImpl(
      // Utils.getUserId(request));
      // List<PineconeCalendar> calendars = new ArrayList<>();
      // request.setAttribute("calendars", calendars);
      // request.setAttribute("message", "request OK (dummy), " +
      // calendars.size()
      // + " calender(s) found");
      request.setAttribute("message", "still testing...");
    } catch (Exception e) {
      log.error(e, e);
      request.setAttribute("message", e.toString());
    }

    request.setAttribute("timestamp", new Date().toString());
    request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

  }

}
