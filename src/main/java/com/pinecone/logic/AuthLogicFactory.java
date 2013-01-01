package com.pinecone.logic;

import javax.servlet.http.HttpServletRequest;

public class AuthLogicFactory {

  public static AuthLogic get(HttpServletRequest request) {
    // TODO Auto-generated method stub
    return new AuthLogicImpl(request);
  }

}
