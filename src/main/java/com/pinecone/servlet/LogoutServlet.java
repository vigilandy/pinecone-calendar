package com.pinecone.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(LogoutServlet.class);
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
   *      response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    AuthLogic logic = AuthLogicFactory.get(request);
    log.info("logging out account '" + logic.getUserId() + "'");
    logic.logout();
    request.getRequestDispatcher(JspPage.LOGGED_OUT).forward(request, response);

  }

}
