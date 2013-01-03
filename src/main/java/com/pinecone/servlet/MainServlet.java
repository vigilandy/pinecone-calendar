package com.pinecone.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = { "/main" })
public class MainServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(MainServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    log.debug("main call");
    request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

  }

}
