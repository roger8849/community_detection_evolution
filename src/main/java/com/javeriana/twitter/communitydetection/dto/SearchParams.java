package com.javeriana.twitter.communitydetection.dto;

import javax.validation.constraints.NotNull;

public class SearchParams {

  @NotNull
  private String text;

  @NotNull
  private Double confidence;

  @NotNull
  private Double support;

  private Integer count;

  private Float percentage;

  @NotNull
  private Boolean shouldIgnoreNumbers;

  @NotNull
  private Boolean areMentionsTopics;

  private Boolean shouldPersistResults;

  @NotNull
  private String loadFrom;

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Double getConfidence() {
    return this.confidence;
  }

  public void setConfidence(Double confidence) {
    this.confidence = confidence;
  }

  public Double getSupport() {
    return this.support;
  }

  public void setSupport(Double support) {
    this.support = support;
  }

  public Float getPercentage() {
    return this.percentage;
  }

  public void setPercentage(Float percentage) {
    this.percentage = percentage;
  }

  public Integer getCount() {
    return this.count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Boolean getShouldIgnoreNumbers() {
    return shouldIgnoreNumbers;
  }

  public void setShouldIgnoreNumbers(Boolean shouldIgnoreNumbers) {
    this.shouldIgnoreNumbers = shouldIgnoreNumbers;
  }

  public Boolean getAreMentionsTopics() {
    return areMentionsTopics;
  }

  public void setAreMentionsTopics(Boolean areMentionsTopics) {
    this.areMentionsTopics = areMentionsTopics;
  }

  public Boolean getShouldPersistResults() {
    return shouldPersistResults == null ? false : shouldPersistResults;
  }

  public void setShouldPersistResults(Boolean shouldPersistResults) {
    this.shouldPersistResults = shouldPersistResults;
  }

  public String getLoadFrom() {
    return loadFrom;
  }

  public void setLoadFrom(String loadFrom) {
    this.loadFrom = loadFrom;
  }
}
