package com.pinecone;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = { "/oauth2callback" })
public class OAuthCallbackServlet extends HttpServlet {

  private static final Logger log = Logger
      .getLogger(OAuthCallbackServlet.class);

  private static final long serialVersionUID = 1L;

}
