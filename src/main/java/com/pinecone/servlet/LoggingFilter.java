package com.pinecone.servlet;

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
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

@WebFilter("/*")
public class LoggingFilter implements Filter {

  private static final Logger log = Logger.getLogger(LoggingFilter.class);

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
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) resp;

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

    // pass the request along the filter chain
    chain.doFilter(request, response);
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  @Override
  public void init(FilterConfig fConfig) throws ServletException {

  }

}
