package com.pinecone.model;

public class PineconeCalendar {

  private String id;
  private String title;

  public PineconeCalendar() {
  }

  public PineconeCalendar(String id, String title) {
    this.id = id;
    this.title = title;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "PineconeCalendar [id=" + id + ", title=" + title + "]";
  }

}
