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

import com.pinecone.constant.RequestAttribute;

@WebFilter("/*")
public class TimestampFilter implements Filter {

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
    req.setAttribute(RequestAttribute.TIMESTAMP, new Date().toString());
    chain.doFilter(req, resp);
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  @Override
  public void init(FilterConfig fConfig) throws ServletException {
  }

}
