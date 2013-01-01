package com.pinecone.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = { "/loginerror" })
public class LoginErrorServlet extends HttpServlet {

  // private static final Logger log =
  // Logger.getLogger(LoginErrorServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setAttribute("message", "login error");
    request.setAttribute("timestamp", new Date().toString());
    request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

  }

}
