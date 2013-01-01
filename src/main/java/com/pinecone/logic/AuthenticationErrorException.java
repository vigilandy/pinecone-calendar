package com.pinecone.logic;

public class AuthenticationErrorException extends Exception {

  private static final long serialVersionUID = 1L;

  public AuthenticationErrorException() {
  }

  public AuthenticationErrorException(String message) {
    super(message);
  }

  public AuthenticationErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthenticationErrorException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AuthenticationErrorException(Throwable cause) {
    super(cause);
  }

}
