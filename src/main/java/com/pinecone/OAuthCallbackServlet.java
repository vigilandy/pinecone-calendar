package com.pinecone;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

@WebServlet(urlPatterns = { "/oauth2callback" })
public class OAuthCallbackServlet extends HttpServlet {

  private static final Logger log = Logger
      .getLogger(OAuthCallbackServlet.class);

  private static final long serialVersionUID = 1L;

  private static String logRequest(HttpServletRequest request) {
    // TODO Auto-generated method stub
    StringBuilder sb = new StringBuilder();

    /* parameters */
    Map<String, String[]> parameters = request.getParameterMap();
    for (Entry<String, String[]> entry : parameters.entrySet()) {
      sb.append("[" + entry.getKey() + "] ");
      String seperator = "";
      for (String value : entry.getValue()) {
        sb.append(seperator + value);
        seperator = ", ";
      }
    }

    return sb.toString();
  }

}
