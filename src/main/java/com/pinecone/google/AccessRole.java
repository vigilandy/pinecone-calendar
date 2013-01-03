package com.pinecone.google;

public enum AccessRole {

  FREEBUSYREADER("freeBusyReader"), OWNER("owner"), READER("reader"), WRITER(
      "writer");

  private final String value;

  private AccessRole(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

}
