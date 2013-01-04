package com.pinecone.servlet.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * logs request url and when trace log is enabled, logs attributes and
 * parameters in the request
 */
@WebFilter("/*")
public class LoggingFilter implements Filter {

  private static final Logger log = Logger.getLogger(LoggingFilter.class);

  private static void logRequest(HttpServletRequest request) {
    log.info("request url: " + request.getRequestURL());
    if (log.isTraceEnabled()) {
      Enumeration<String> attributeNames = request.getAttributeNames();
      while (attributeNames.hasMoreElements()) {
        String attributeName = attributeNames.nextElement();
        log.debug(String.format("\tattribute %s=%s", attributeName,
            request.getAttribute(attributeName)));
      }

      Map<String, String[]> parameters = request.getParameterMap();
      for (Entry<String, String[]> entry : parameters.entrySet()) {
        for (String value : entry.getValue()) {
          log.debug(String.format("\tparameter %s=%s", entry.getKey(), value));
        }
      }
    }
  }

  /**
   * @see Filter#destroy()
   */
  @Override
  public void destroy() {
  }

  /**
   * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
   */
  @Override
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    logRequest((HttpServletRequest) req);
    chain.doFilter(req, resp);
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  @Override
  public void init(FilterConfig fConfig) throws ServletException {
  }

}
