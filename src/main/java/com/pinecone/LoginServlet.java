package com.pinecone;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pinecone.logic.AuthenticationLogic;

@WebServlet(urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(LoginServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    log.info("get request for " + request.getRequestURI());

    AuthenticationLogic logic = new AuthenticationLogic(request);

    if (logic.isLoggedIn()) {
      response.sendRedirect("/main");
      return;
    }

    request.setAttribute("message", "login request");
    request.setAttribute("timestamp", new Date().toString());
    request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

  }

}
