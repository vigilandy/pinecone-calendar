package com.pinecone.servlet.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.log4j.Logger;

import com.pinecone.constant.RequestAttribute;

/**
 * test
 */
@WebFilter("/*")
public class TimestampFilter implements Filter {

  private static final Logger log = Logger.getLogger(TimestampFilter.class);

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
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    log.debug("start");
    request.setAttribute(RequestAttribute.TIMESTAMP, new Date().toString());

    // pass the request along the filter chain
    chain.doFilter(request, response);

    log.debug("end");
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  @Override
  public void init(FilterConfig fConfig) throws ServletException {
  }

}
