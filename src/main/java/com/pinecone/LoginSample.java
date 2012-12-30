package com.pinecone;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = { "/login" })
public class LoginSample extends HttpServlet {

  private static final Logger log = Logger.getLogger(LoginSample.class);
  private static final long serialVersionUID = 1L;

}
