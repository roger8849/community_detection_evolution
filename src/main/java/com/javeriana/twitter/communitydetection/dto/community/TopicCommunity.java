package com.javeriana.twitter.communitydetection.dto.community;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;

public class TopicCommunity {
  private String communityName;
  private Set<String> topics;
  private Set<TwitterVertex> members;

  public TopicCommunity() {
    super();
  }


  public TopicCommunity(Set<String> topics, Set<TwitterVertex> members) {
    super();
    this.topics = topics;
    this.members = members;
  }

  public String getCommunityName() {
    if (topics != null && !topics.isEmpty()) {
      return topics.stream().collect(Collectors.joining(","));
    } else {
      return "no topics in the community to build the name";
    }
  }

  public Set<TwitterVertex> getMembers() {
    return this.members;
  }

  public void setMembers(Set<TwitterVertex> members) {
    this.members = members;
  }

  public Set<String> getTopics() {
    return topics;
  }

  public void setTopics(Set<String> topics) {
    this.topics = topics;
  }

  @Override
  public String toString() {
    return this.communityName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof TwitterVertex)) {
      return false;
    }
    TopicCommunity community = (TopicCommunity) obj;
    return Objects.equals(this.topics, community.getTopics());
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.topics);
  }

}
