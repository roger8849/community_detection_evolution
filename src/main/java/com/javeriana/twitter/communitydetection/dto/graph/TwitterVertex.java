package com.javeriana.twitter.communitydetection.dto.graph;

import java.util.Objects;

public class TwitterVertex {
  private String screenName;
  private int popularityIndex;
  private String includeBy;
  private String tweetId;
  private Double normalizedPopularity;

  public TwitterVertex(String screenName) {
    super();
    this.screenName = screenName;
  }

  public TwitterVertex(String screenName, int popularityIndex) {
    super();
    this.screenName = screenName;
    this.popularityIndex = popularityIndex;
  }

  public TwitterVertex(String screenName, int popularityIndex, String includeBy, String tweetId) {
    super();
    this.screenName = screenName;
    this.popularityIndex = popularityIndex;
    this.includeBy = includeBy;
    this.tweetId = tweetId;
  }

  public String getScreenName() {
    return this.screenName;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  public int getPopularityIndex() {
    return popularityIndex;
  }

  public void setPopularityIndex(int popularityIndex) {
    this.popularityIndex = popularityIndex;
  }

  public String getIncludeBy() {
    return includeBy;
  }

  public void setIncludeBy(String includeBy) {
    this.includeBy = includeBy;
  }

  public String getTweetId() {
    return tweetId;
  }

  public void setTweetId(String tweetId) {
    this.tweetId = tweetId;
  }

  public Double getNormalizedPopularity() {
    return normalizedPopularity;
  }

  public void setNormalizedPopularity(Double normalizedPopularity) {
    this.normalizedPopularity = normalizedPopularity;
  }

  @Override
  public String toString() {
    return this.screenName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TwitterVertex)) {
      return false;
    }
    TwitterVertex vertex = (TwitterVertex) obj;
    return Objects.equals(this.screenName, vertex.getScreenName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.screenName);
  }

}
