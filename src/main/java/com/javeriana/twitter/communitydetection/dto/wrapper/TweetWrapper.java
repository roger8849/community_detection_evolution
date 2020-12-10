package com.javeriana.twitter.communitydetection.dto.wrapper;

import com.javeriana.twitter.communitydetection.dto.tweet.CustomTweet;
import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tweets_wrapper")
public class TweetWrapper {

  @Id
  private Long id;
  private CustomTweet customTweet;
  private boolean processed;
  private Date processedAt;

  public TweetWrapper() {
    super();
    this.processed = false;
    this.customTweet = new CustomTweet();
    this.id = customTweet.getId();
    this.processedAt = new Date();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CustomTweet getCustomTweet() {
    return customTweet;
  }

  public void setCustomTweet(CustomTweet customTweet) {
    this.customTweet = customTweet;
  }

  public boolean isProcessed() {
    return processed;
  }

  public void setProcessed(boolean processed) {
    this.processed = processed;
  }

  public Date getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Date processedAt) {
    this.processedAt = processedAt;
  }
}
