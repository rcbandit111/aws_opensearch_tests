package com.aws.opensearch.tests.dto;

import java.util.Map;

public class BaseIndexDto {

  private String index;
  private String id;
  private Map<String, Object> document;
  private Long updateSpecificVersion;

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, Object> getDocument() {
    return document;
  }

  public void setDocument(Map<String, Object> document) {
    this.document = document;
  }

  public Long getUpdateSpecificVersion() {
    return updateSpecificVersion;
  }

  public void setUpdateSpecificVersion(Long specificVersion) {
    this.updateSpecificVersion = specificVersion;
  }
  
}
