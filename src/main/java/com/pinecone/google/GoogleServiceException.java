package com.pinecone.google;

public class GoogleServiceException extends Exception {

  private static final long serialVersionUID = 1L;

  GoogleServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
