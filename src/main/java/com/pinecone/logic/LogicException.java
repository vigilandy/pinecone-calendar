package com.pinecone.logic;

public class LogicException extends Exception {

  private static final long serialVersionUID = 1L;

  public LogicException(String message) {
    super(message);
  }

  public LogicException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogicException(Throwable cause) {
    super(cause);
  }

}
