package com.javeriana.twitter.communitydetection.service;

import com.javeriana.twitter.communitydetection.dto.wrapper.TweetWrapper;
import java.util.List;
import java.util.Set;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.UrlEntity;
import com.javeriana.twitter.communitydetection.dto.SearchParams;

public interface TwitterService {

  List<Tweet> getHomeTimelineTweets();

  Tweet getTweetByTweetId(String tweetId);

  List<Tweet> getTweetsBySearchParams(SearchParams searchParams);

  String getTweetIdFromURLEntity(UrlEntity urlEntity);

  Set<String> getHashtagsAndMentionsFromTweet(Tweet tweet, Boolean areMentionsTopics);

  Tweet saveTweetInMongoDatabase(String tweetId);

  TweetWrapper save(Tweet tweet);

  void streamTweetsInDatabase(String text);

  void stopTwitterStreaming();
}
