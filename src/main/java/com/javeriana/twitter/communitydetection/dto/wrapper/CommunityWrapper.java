package com.javeriana.twitter.communitydetection.dto.wrapper;

import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "community_wrapper")
public class CommunityWrapper {
  private UUID id;
  private Set<TopicCommunity> topicCommunities;
  private Date processedAt;

  public CommunityWrapper() {
    super();
  }

  public CommunityWrapper(UUID id,
      Set<TopicCommunity> topicCommunities, Date processedAt) {
    this.id = id;
    this.topicCommunities = topicCommunities;
    this.processedAt = processedAt;
  }


  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Set<TopicCommunity> getTopicCommunities() {
    return topicCommunities;
  }

  public void setTopicCommunities(
      Set<TopicCommunity> topicCommunities) {
    this.topicCommunities = topicCommunities;
  }

  public Date getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Date processedAt) {
    this.processedAt = processedAt;
  }
}
