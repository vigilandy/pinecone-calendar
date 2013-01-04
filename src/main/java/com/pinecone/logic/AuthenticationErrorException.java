package com.pinecone.logic;

public class AuthenticationErrorException extends Exception {

  private static final long serialVersionUID = 1L;

  public AuthenticationErrorException(String message) {
    super(message);
  }

  public AuthenticationErrorException(Throwable cause) {
    super(cause);
  }

}
