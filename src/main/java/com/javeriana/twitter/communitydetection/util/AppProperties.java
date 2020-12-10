package com.javeriana.twitter.communitydetection.util;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties("app")
public class AppProperties {

  private Geocode geocode;

  @Value("${app.search-count}")
  private Integer searchCount;

  private Double confidence;

  private Double support;

  @Value("${app.stop-words}.split(',')")
  private List<String> stopWords;

  @Value("${app.should-ignore-numbers}")
  private boolean shouldIgnoreNumbers;

  @Value("#{'${app.stream.track-words:Colombia,Bogotá}'.split(',')}")
  private List<String> streamTrackWords;

  public Geocode getGeocode() {
    return this.geocode;
  }

  public void setGeocode(Geocode geocode) {
    this.geocode = geocode;
  }

  public Integer getSearchCount() {
    return this.searchCount;
  }

  public void setSearchCount(Integer searchCount) {
    this.searchCount = searchCount;
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

  public List<String> getStopWords() {
    return stopWords;
  }

  public void setStopWords(List<String> stopWords) {
    if (stopWords != null && !stopWords.isEmpty()) {
      stopWords.replaceAll(String::toLowerCase);
    }
    this.stopWords = stopWords;
  }

  public boolean isShouldIgnoreNumbers() {
    return shouldIgnoreNumbers;
  }

  public void setShouldIgnoreNumbers(boolean shouldIgnoreNumbers) {
    this.shouldIgnoreNumbers = shouldIgnoreNumbers;
  }

  public List<String> getStreamTrackWords() {
    return streamTrackWords;
  }

  public void setStreamTrackWords(List<String> streamTrackWords) {
    this.streamTrackWords = streamTrackWords;
  }

  public static class Geocode {

    private Double longitude;
    private Double latitude;
    private Integer radius;

    public Double getLongitude() {
      return this.longitude;
    }

    public void setLongitude(Double longitude) {
      this.longitude = longitude;
    }

    public Double getLatitude() {
      return this.latitude;
    }

    public void setLatitude(Double latitude) {
      this.latitude = latitude;
    }

    public Integer getRadius() {
      return this.radius;
    }

    public void setRadius(Integer radius) {
      this.radius = radius;
    }

  }

}
