package com.javeriana.twitter.communitydetection.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.springframework.social.twitter.api.Tweet;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;

public interface TopicCommunityService {

  FPGrowthModel<String> associationRulesByKey(SearchParams searchParams);


  Set<TopicCommunity> findCommunities(List<Tweet> tweets, SearchParams searchParams);

  Set<TopicCommunity> findCommunitiesByKey(SearchParams searchParams);

  byte[] findCommunityImageByKey(SearchParams searchParams) throws IOException;

  List<Set<String>> getNamedEntitiesFromTweets(SearchParams searchParams);

  List<Set<String>> getHashtagsAndMentionsFromTweets(SearchParams searchParams);

  List<Set<String>> getAllTopicsFromTweets(SearchParams searchParams);

  FPGrowthModel<String> getFrequencyModelFromTweetList(List<Set<String>> transactionsList,
      SearchParams searchParams);


}
