package com.pinecone.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;
import com.pinecone.servlet.json.AbstractRESTServlet;

/**
 * Servlet Filter implementation class RESTfulServiceFilter
 */
@WebFilter("/rest/*")
public class RESTfulServiceFilter implements Filter {

  /**
   * @see Filter#destroy()
   */
  public void destroy() {
  }

  /**
   * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
   */
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    AuthLogic logic = AuthLogicFactory.get((HttpServletRequest) req);
    if (logic.isLoggedIn()) {
      // pass the request along the filter chain
      chain.doFilter(req, resp);
    } else {
      AbstractRESTServlet.writeJsonError((HttpServletResponse) resp,
          "user not authenticated");
    }
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  public void init(FilterConfig fConfig) throws ServletException {
  }

}
