package com.javeriana.twitter.communitydetection.dto.graph;

import java.util.Objects;

public class TwitterEdge {
  private TwitterVertex source;
  private TwitterVertex destination;
  private Integer weight;

  public TwitterEdge(TwitterVertex source, TwitterVertex destination) {
    super();
    this.source = source;
    this.destination = destination;
    this.weight = 1;
  }

  public Integer getWeight() {
    return this.weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public TwitterVertex getSource() {
    return this.source;
  }

  public void setSource(TwitterVertex source) {
    this.source = source;
  }

  public TwitterVertex getDestination() {
    return this.destination;
  }

  public void setDestination(TwitterVertex destination) {
    this.destination = destination;
  }

  @Override
  public String toString() {
    return String.valueOf(this.weight);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TwitterEdge)) {
      return false;
    }
    TwitterEdge edge = (TwitterEdge) obj;
    return Objects.equals(this.source, edge.getSource())
        && Objects.equals(this.destination, edge.destination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.source, this.destination);
  }

}
