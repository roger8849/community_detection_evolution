package com.javeriana.twitter.communitydetection.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.social.twitter.api.Tweet;

public interface TweetRepository extends MongoRepository<Tweet, Long> {
  List<Tweet> findByCreatedAtBetween(Date startDate, Date endDate);
}
