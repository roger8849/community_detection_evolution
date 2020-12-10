package com.javeriana.twitter.communitydetection.service.impl;

import com.javeriana.twitter.communitydetection.dto.tweet.CustomTweet;
import com.javeriana.twitter.communitydetection.dto.wrapper.TweetWrapper;
import com.javeriana.twitter.communitydetection.repository.CustomTweetRepository;
import com.javeriana.twitter.communitydetection.repository.TweetWrapperRepository;
import com.javeriana.twitter.communitydetection.util.AppProperties;
import com.javeriana.twitter.communitydetection.util.CommunityBeanUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Entities;
import org.springframework.social.twitter.api.FilterStreamParameters;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamDeleteEvent;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.StreamWarningEvent;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.UrlEntity;
import org.springframework.stereotype.Service;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.repository.TweetRepository;
import com.javeriana.twitter.communitydetection.service.TwitterService;
import com.javeriana.twitter.communitydetection.util.ApplicationConstants;
import com.javeriana.twitter.communitydetection.util.SearchUtils;

@Service
public class DefaultTwitterService implements TwitterService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultTwitterService.class);

  @Autowired
  private Twitter twitter;

  @Autowired
  private SearchUtils searchUtils;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private CustomTweetRepository customTweetRepository;

  @Autowired
  private TweetWrapperRepository tweetWrapperRepository;

  private Stream stream;

  @Override
  public List<Tweet> getTweetsBySearchParams(SearchParams searchParams) {
    LOG.debug("Getting the tweets with the search key: {}", searchParams);
    List<Tweet> tweets = this.twitter.searchOperations()
        .search(this.searchUtils.getDefaultSearchParameters(searchParams)).getTweets();
    LOG.debug("Ending the tweets getting with the search key: {}", searchParams);
    return tweets;
  }

  @Override
  public List<Tweet> getHomeTimelineTweets() {
    LOG.debug("Getting the tweets with the search key: {}");
    List<Tweet> tweets = this.twitter.timelineOperations().getUserTimeline(10);
    LOG.debug("Ending the tweets getting with the search key: {}");
    return tweets;
  }

  @Override
  public Tweet getTweetByTweetId(String tweetId) {
    try {
      LOG.debug("Getting tweet by Tweet id : {}", tweetId);
      Tweet tweet = this.twitter.timelineOperations().getStatus(Long.parseLong(tweetId));
      LOG.debug("End Getting tweet by Tweet id : {}", tweetId);
      return tweet;
    } catch (Exception e) {
      LOG.warn("No tweet found with id: " + tweetId);
      return null;
    }
  }

  @Override
  public Tweet saveTweetInMongoDatabase(String tweetId) {
    this.getTweetByTweetId(tweetId);
    LOG.debug("Saving tweet");
    Tweet tweet = this.getTweetByTweetId(tweetId);
    return this.saveTweet(tweet);
  }

  private Tweet saveTweet(Tweet tweet){
    return this.tweetRepository.save(tweet);
  }

  @Override
  public TweetWrapper save(Tweet tweet) {
    /*if (tweet != null) {
      this.tweetRepository.save(tweet);
    }
    return this.tweetRepository.save(tweet); */
    TweetWrapper wrapper = new TweetWrapper();
    if (tweet != null) {
      wrapper.setProcessed(false);
      CustomTweet customTweet = CommunityBeanUtils.mapCustomTweetFromTweet(tweet);
      wrapper.setCustomTweet(customTweet);
      wrapper = this.tweetWrapperRepository.save(wrapper);
    }
    return wrapper;
  }

  @Override
  public void streamTweetsInDatabase(String text) {
    StreamListener listener = new StreamListener() {
      
      @Override
      public void onWarning(StreamWarningEvent warningEvent) {
        
      }
      
      @Override
      public void onTweet(Tweet tweet) {
        save(tweet);
      }
      
      @Override
      public void onLimit(int numberOfLimitedTweets) {
        
      }
      
      @Override
      public void onDelete(StreamDeleteEvent deleteEvent) {

      }
    };

    List<StreamListener> listeners = new ArrayList<>();
    listeners.add(listener);
    FilterStreamParameters parameters = new FilterStreamParameters();

    List<String> streamTrackWords = null;
    if(StringUtils.isEmpty(text)){
      streamTrackWords = appProperties.getStreamTrackWords();
    } else {
      streamTrackWords = Arrays.asList(text.split(","));
    }


    streamTrackWords.forEach(parameters::track);
    parameters.addLocation(-79.178249f, -4.151146f, -67.443110f, 12.421118f);
    stream = this.twitter.streamingOperations().filter(parameters, listeners);
    //stream = this.twitter.streamingOperations().sample(listeners);
  }


  @Override
  public String getTweetIdFromURLEntity(UrlEntity urlEntity) {
    String twitterId = null;
    String expandedUrl = urlEntity.getExpandedUrl();
    if (null != expandedUrl
        && StringUtils.containsIgnoreCase(expandedUrl, ApplicationConstants.TWITTER_COM)) {
      String[] splittedString = expandedUrl.split("status/");
      twitterId = splittedString[1].replaceAll("/", "");
    }
    return twitterId;
  }

  @Override
  public Set<String> getHashtagsAndMentionsFromTweet(Tweet tweet, Boolean areMentionsTopics) {
    Entities entity = tweet.getEntities();
    Set<String> structuredTopics = new HashSet<>();
    if (entity != null && !entity.getHashTags().isEmpty()) {
      structuredTopics.addAll(entity.getHashTags().parallelStream()
          .map(h -> h.getText().toLowerCase()).collect(Collectors.toSet()));
      if (Boolean.TRUE.equals(areMentionsTopics)) {
        structuredTopics.addAll(entity.getMentions().parallelStream()
            .map(m -> m.getScreenName().toLowerCase()).collect(Collectors.toSet()));
      }
    }
    return structuredTopics;
  }

  @Override
  public void stopTwitterStreaming(){
    if(this.stream != null){
      this.stream.close();
    }
  }
}
