package com.pinecone.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pinecone.constant.RequestAttribute;

@WebServlet(urlPatterns = { "/loginerror" })
public class LoginErrorServlet extends HttpServlet {

  // private static final Logger log =
  // Logger.getLogger(LoginErrorServlet.class);
  private static final long serialVersionUID = 1L;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.setAttribute(RequestAttribute.MESSAGE, "login error");
    request.getRequestDispatcher(JspPage.ERROR).forward(request, response);

  }

}
