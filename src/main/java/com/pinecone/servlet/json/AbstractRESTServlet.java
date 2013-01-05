package com.pinecone.servlet.json;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public abstract class AbstractRESTServlet extends HttpServlet {

  protected static final String ACTION_GET = "get";
  private static final Logger log = Logger.getLogger(AbstractRESTServlet.class);
  private static final long serialVersionUID = 1L;

  private static void close(Writer writer) {
    try {
      writer.close();
    } catch (IOException e) {
      log.warn(e, e);
    }
  }

  /**
   * Write given Java object as JSON to the output of the current response.
   * 
   * @param object
   *          Any Java Object to be written as JSON to the output of the current
   *          response.
   * @throws IOException
   *           If something fails at IO level.
   */
  protected static void writeJson(HttpServletResponse response, Object object)
      throws IOException {
    String json = new Gson().toJson(object);
    writeJsonString(response, json);
  }

  public static void writeJsonError(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
    writeJsonString(response, String.format("{\"error\":\"%s\"}", message));
  }

  protected static void writeJsonString(HttpServletResponse response,
      String json) throws IOException {
    log.debug("writing json string: " + json);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    Writer writer = null;

    try {
      writer = response.getWriter();
      writer.write(json);
    } finally {
      close(writer);
    }
  }

}
