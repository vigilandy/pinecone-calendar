package com.pinecone.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.pinecone.constant.RequestAttribute;
import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;

@WebServlet(urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(LoginServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    AuthLogic logic = AuthLogicFactory.get(request);

    if (logic.isLoggedIn()) {

      request.setAttribute(RequestAttribute.MESSAGE,
          "logged in as '" + logic.getUserId() + "'");
      request.getRequestDispatcher(JspPage.MAIN).forward(request, response);

    } else {

      String redirectLocation = logic.getRedirectUrl();
      log.info("sending redirect: " + redirectLocation);
      response.sendRedirect(redirectLocation);

    }

  }

}
