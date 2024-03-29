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

import com.pinecone.constant.SessionAttribute;
import com.pinecone.logic.AuthLogic;
import com.pinecone.logic.AuthLogicFactory;

/**
 * check user authentication
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

  private static void setLoggedInFlag(HttpServletRequest request) {
    AuthLogic logic = AuthLogicFactory.get(request);
    request.getSession().setAttribute(SessionAttribute.LOGGED_IN,
        logic.isLoggedIn());
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
    setLoggedInFlag((HttpServletRequest) req);
    chain.doFilter(req, resp);
  }

  /**
   * @see Filter#init(FilterConfig)
   */
  @Override
  public void init(FilterConfig fConfig) throws ServletException {
  }

}
