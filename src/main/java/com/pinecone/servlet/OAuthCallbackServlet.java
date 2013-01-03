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
import com.pinecone.logic.AuthenticationErrorException;

@WebServlet(urlPatterns = { "/oauth2callback" })
public class OAuthCallbackServlet extends HttpServlet {

  private static final Logger log = Logger
      .getLogger(OAuthCallbackServlet.class);

  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    AuthLogic logic = AuthLogicFactory.get(request);
    try {
      logic.handleCallback(response);
      response.sendRedirect(request.getContextPath());
    } catch (AuthenticationErrorException e) {
      log.error(e, e);
      response.sendRedirect(request.getContextPath() + "/loginerror");
    }

  }

}
