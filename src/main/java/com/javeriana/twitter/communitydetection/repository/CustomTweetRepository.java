package com.javeriana.twitter.communitydetection.repository;

import com.javeriana.twitter.communitydetection.dto.tweet.CustomTweet;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomTweetRepository extends MongoRepository<CustomTweet, Long> {
  List<CustomTweet> findByCreatedAtBetween(Date startDate, Date endDate);
}
