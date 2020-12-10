package com.javeriana.twitter.communitydetection.service.impl;

import com.javeriana.twitter.communitydetection.dto.wrapper.CommunityWrapper;
import com.javeriana.twitter.communitydetection.dto.wrapper.TweetWrapper;
import com.javeriana.twitter.communitydetection.repository.CommunityWrapperRepository;
import com.javeriana.twitter.communitydetection.repository.CustomTweetRepository;
import com.javeriana.twitter.communitydetection.repository.TweetRepository;
import com.javeriana.twitter.communitydetection.repository.TweetWrapperRepository;
import com.javeriana.twitter.communitydetection.util.CommunityBeanUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.AssociationRules.Rule;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowth.FreqItemset;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.social.twitter.api.Entities;
import org.springframework.social.twitter.api.MentionEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.UrlEntity;
import org.springframework.stereotype.Service;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;
import com.javeriana.twitter.communitydetection.service.TopicCommunityService;
import com.javeriana.twitter.communitydetection.service.TwitterService;
import com.javeriana.twitter.communitydetection.util.AppProperties;
import com.javeriana.twitter.communitydetection.util.ApplicationConstants;
import com.javeriana.twitter.communitydetection.util.GraphUtils;
import com.javeriana.twitter.communitydetection.util.NaturalLanguageUtils;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

@Service
@Primary
public class DefaultTopicCommunityService implements TopicCommunityService {

  public static final Logger LOG = LoggerFactory.getLogger(DefaultTopicCommunityService.class);

  public static final double NORMALIZE_MIN_VALUE = 0;
  public static final double NORMALIZE_NEW_MAX = 1;
  public static final double NORMALIZE_NEW_MIN = 0;

  @Autowired
  private TwitterService twitterService;

  @Autowired
  private JavaSparkContext javaSparkContext;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private NaturalLanguageUtils naturalLanguageUtils;

  @Autowired
  private TweetRepository tweetRepository;

  @Autowired
  private CustomTweetRepository customTweetRepository;

  @Autowired
  private TweetWrapperRepository tweetWrapperRepository;

  @Autowired
  private CommunityWrapperRepository communityWrapperRepository;

  @Override
  public byte[] findCommunityImageByKey(SearchParams searchParams) throws IOException {
    Set<TopicCommunity> communities = this.findCommunitiesByKey(searchParams);
    Graph<TwitterVertex, TwitterEdge> graph = this.createGraphFromTopicCommunityList(communities);
    List<Graph<TwitterVertex, TwitterEdge>> graphs =
        this.createGraphListFormCommunityList(communities);
    GraphUtils.createCommunityImagesFromGraphList(graphs, "hashtag");
    return GraphUtils.getImageBytesFromGraph(graph);
  }

  @Override
  public Set<TopicCommunity> findCommunitiesByKey(SearchParams searchParams) {
    List<Tweet> tweets = null;
    List<TweetWrapper> tweetWrapperList = null;
    if (ApplicationConstants.LOAD_FROM_DATABASE.equals(searchParams.getLoadFrom())) {
      tweetWrapperList = this.tweetWrapperRepository.getAllByProcessed(false);
      tweets = tweetWrapperList.parallelStream()
          .map(w -> CommunityBeanUtils.mapTweetFromCustomTweet(w.getCustomTweet()))
          .collect(Collectors.toList());
    } else {
      tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    }
    Set<TopicCommunity> topicCommunities = null;

    if (tweets != null && !tweets.isEmpty()) {
      topicCommunities = this.findCommunities(tweets, searchParams);

    } else {
      topicCommunities = new HashSet<>();
    }

    if (topicCommunities != null && !topicCommunities.isEmpty() && searchParams
        .getShouldPersistResults()) {
      CommunityWrapper communityWrapper = new CommunityWrapper(UUID.randomUUID(), topicCommunities, new Date(System.currentTimeMillis()));
      this.communityWrapperRepository.save(communityWrapper);
      if(tweetWrapperList != null && !tweetWrapperList.isEmpty() ){
        tweetWrapperList.forEach( w -> w.setProcessed(true));
        this.tweetWrapperRepository.save(tweetWrapperList);
      }
    }
    return topicCommunities;
  }

  @Override
  public FPGrowthModel<String> associationRulesByKey(SearchParams searchParams) {
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    // return this.getFrequencyModelFromTweetList(tweets, searchParams);
    return null;
  }

  @Override
  public Set<TopicCommunity> findCommunities(List<Tweet> tweets, SearchParams searchParams) {
    double confidence = searchParams.getConfidence() == null ? this.appProperties.getConfidence()
        : searchParams.getConfidence();

    List<Set<String>> transactionsList = new ArrayList<>();
    Map<String, Set<Tweet>> tweetsPerTopic = new HashMap<>();
    for (Tweet tweet : tweets) {
      Set<String> topics = this.getAllTopicsFromTweet(tweet, searchParams.getShouldIgnoreNumbers(),
          searchParams.getAreMentionsTopics());
      if (topics != null && !topics.isEmpty()) {
        transactionsList.add(topics);
        for (String topic : topics) {
          Set<Tweet> tweetsPerTopicSet = tweetsPerTopic.get(topic);
          if (tweetsPerTopicSet == null || tweetsPerTopic.isEmpty()) {
            tweetsPerTopicSet = new HashSet<>();
          }
          tweetsPerTopicSet.add(tweet);
          tweetsPerTopic.put(topic, tweetsPerTopicSet);
        }
      }
    }

    Map<String, Set<TwitterVertex>> reciprocityMap =
        this.createReciprocityMapFromTweets(tweetsPerTopic);

    FPGrowthModel<String> model =
        this.getFrequencyModelFromTweetList(transactionsList, searchParams);
    // List<Rule<String>> associationRules =
    // model.generateAssociationRules(confidence).toJavaRDD().collect();
    List<FreqItemset<String>> frequencyItems = model.freqItemsets().toJavaRDD().collect();
    Set<TopicCommunity> communities = new HashSet<>();

    for (FreqItemset<String> freqItemset : frequencyItems) {
      Set<String> communityTopics = new HashSet<>(freqItemset.javaItems());
      if (communityTopics.size() > 1) {
        communities.add(new TopicCommunity(communityTopics,
            this.getCommunityMembersFromTopicList(communityTopics, reciprocityMap)));
      }
      // else {
      // float percentage = freqItemset.freq() * 100 / tweets.size();
      // if (percentage > 0.4 && percentage < 0.8) {
      // communities.add(new TopicCommunity(communityName, new HashSet<>()));
      // }
      // }
    }

    return communities;
  }

  @Override
  public FPGrowthModel<String> getFrequencyModelFromTweetList(List<Set<String>> transactionsList,
      SearchParams searchParams) {
    double confidence = searchParams.getConfidence() == null ? this.appProperties.getConfidence()
        : searchParams.getConfidence();

    double support = searchParams.getSupport() == null ? this.appProperties.getSupport()
        : searchParams.getSupport();

    JavaRDD<Set<String>> transactions = this.javaSparkContext.parallelize(transactionsList);

    FPGrowth fpg = new FPGrowth().setMinSupport(support);

    FPGrowthModel<String> model = fpg.run(transactions);

    List<Rule<String>> associationRules =
        model.generateAssociationRules(confidence).toJavaRDD().collect();
    List<FreqItemset<String>> collect = model.freqItemsets().toJavaRDD().collect();

    for (FreqItemset<String> freqItemset : collect) {
      LOG.debug("Frequency string: " + freqItemset.toString() + " Number of appears: "
          + freqItemset.freq());
    }
    for (AssociationRules.Rule<String> rule : associationRules) {
      LOG.debug(rule.javaAntecedent() + " => " + rule.javaConsequent() + ", " + rule.confidence());
    }
    return model;
  }

  @Override
  public List<Set<String>> getNamedEntitiesFromTweets(SearchParams searchParams) {
    List<Set<String>> entitesInTweets = new ArrayList<>();
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    List<String> textTweets =
        tweets.parallelStream().map(Tweet::getText).collect(Collectors.toList());
    for (String text : textTweets) {
      this.getNamedEntitiesFromText(text, searchParams.getShouldIgnoreNumbers());
    }
    return entitesInTweets;
  }

  @Override
  public List<Set<String>> getHashtagsAndMentionsFromTweets(SearchParams searchParams) {
    List<Set<String>> hashtagMentions = new ArrayList<>();
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    for (Tweet tweet : tweets) {
      hashtagMentions.add(this.twitterService.getHashtagsAndMentionsFromTweet(tweet,
          searchParams.getAreMentionsTopics()));
    }
    return hashtagMentions;
  }

  @Override
  public List<Set<String>> getAllTopicsFromTweets(SearchParams searchParams) {
    List<Set<String>> allTopics = new ArrayList<>();
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    for (Tweet tweet : tweets) {
      allTopics.add(this.getAllTopicsFromTweet(tweet, searchParams.getShouldIgnoreNumbers(),
          searchParams.getAreMentionsTopics()));
    }
    return allTopics;
  }

  private Set<TwitterVertex> getCommunityMembersFromTopicList(Set<String> communityTopics,
      Map<String, Set<TwitterVertex>> reciprocityMap) {
    Set<TwitterVertex> communityMembers = new HashSet<>();
    for (String topic : communityTopics) {
      Set<TwitterVertex> nonNormalizedMembers = reciprocityMap.get(topic);
      if (nonNormalizedMembers != null && !nonNormalizedMembers.isEmpty()) {
        normalizeReciprocityMembers(nonNormalizedMembers);
      }
      communityMembers.addAll(nonNormalizedMembers);
    }
    return communityMembers;
  }

  private void normalizeReciprocityMembers(Set<TwitterVertex> members) {
    List<Integer> popularities = members.stream().map(TwitterVertex::getPopularityIndex)
        .collect(Collectors.toList());
    final double maxValue = Collections.max(popularities);
    members.forEach(member -> member
        .setNormalizedPopularity(minMaxNormalization(member.getPopularityIndex(), maxValue)));

  }

  private Double minMaxNormalization(double value, double max) {
    Double minMax =
        ((value - NORMALIZE_MIN_VALUE) / (max - NORMALIZE_MIN_VALUE)) * (NORMALIZE_NEW_MAX
            - NORMALIZE_NEW_MIN) + (NORMALIZE_NEW_MIN);
    return minMax;
  }


  private Map<String, Set<TwitterVertex>> createReciprocityMapFromTweets(
      Map<String, Set<Tweet>> tweetsPerTopic) {
    Map<String, Set<TwitterVertex>> popularityScoreByTopic = new HashMap<>();

    for (Map.Entry<String, Set<Tweet>> entry : tweetsPerTopic.entrySet()) {
      Set<Tweet> tweets = entry.getValue();
      Map<TwitterVertex, TwitterVertex> usersMap = new HashMap<>();
      for (Tweet tweet : tweets) {
        this.getUsersReciprocityFromTweet(usersMap, tweet);
      }
      popularityScoreByTopic.put(entry.getKey(), usersMap.keySet());
    }
    return popularityScoreByTopic;
  }


  private void getUsersReciprocityFromTweet(Map<TwitterVertex, TwitterVertex> usersMap,
      Tweet tweet) {
    Entities entities = tweet.getEntities();
    Tweet retweetedTweet = tweet.getRetweetedStatus();
    tweetIsARetweet(usersMap, entities, retweetedTweet, tweet);
    tweetIsAReply(usersMap, tweet);
    tweetIsMentioningSomebody(usersMap, entities, tweet);
  }

  private void tweetIsARetweet(Map<TwitterVertex, TwitterVertex> usersMap, Entities entities,
      Tweet retweetedTweet, Tweet tweet) {
    // Detecting if is a retweet
    String type = ApplicationConstants.RETWEET;
    boolean isRetweetQuoteTweet = false;
    if (null == retweetedTweet) {
      List<UrlEntity> urls = entities.getUrls();
      if (urls != null && !urls.isEmpty()) {
        for (UrlEntity urlEntity : urls) {
          String tweetId = this.twitterService.getTweetIdFromURLEntity(urlEntity);
          if (null != tweetId) {
            retweetedTweet = this.twitterService.getTweetByTweetId(tweetId);
            if (null != retweetedTweet) {
              isRetweetQuoteTweet = true;
              type = ApplicationConstants.QUOTE_TWEET;
            }
          }
        }
      }
    } else {
      isRetweetQuoteTweet = true;
    }
    if (isRetweetQuoteTweet) {
      TwitterVertex originalUser = new TwitterVertex(retweetedTweet.getFromUser(),
          retweetedTweet.getRetweetCount() + retweetedTweet.getFavoriteCount() + 1, type,
          retweetedTweet.getIdStr());
      TwitterVertex currentUser = new TwitterVertex(tweet.getFromUser(),
          tweet.getFavoriteCount() + 1, ApplicationConstants.ORIGIN, tweet.getIdStr());
      usersMap.put(originalUser, originalUser);
      usersMap.put(currentUser, currentUser);
    }
  }

  private void tweetIsAReply(Map<TwitterVertex, TwitterVertex> usersMap, Tweet tweet) {
    // Detecting if is a reply

    if (null != tweet.getInReplyToUserId()) {
      String inReplyToScreenName = tweet.getInReplyToScreenName();
      TwitterVertex inReplyVertex = new TwitterVertex(inReplyToScreenName);
      TwitterVertex existingUser = usersMap.get(inReplyVertex);
      if (existingUser == null) {
        inReplyVertex =
            new TwitterVertex(inReplyToScreenName, 1, ApplicationConstants.REPLY, tweet.getIdStr());
        usersMap.put(inReplyVertex, inReplyVertex);
      } else {
        existingUser.setPopularityIndex(existingUser.getPopularityIndex() + 1);
        usersMap.put(existingUser, existingUser);
      }
    }
  }

  private void tweetIsMentioningSomebody(Map<TwitterVertex, TwitterVertex> usersMap,
      Entities entities, Tweet tweet) {
    // Detecting if is mentioning somebody else
    List<MentionEntity> mentions = entities.getMentions();
    if (mentions != null && !mentions.isEmpty()) {
      for (MentionEntity mentionEntity : mentions) {
        String mentionedScreenName = mentionEntity.getScreenName();
        TwitterVertex mentionedUser = new TwitterVertex(mentionedScreenName);
        TwitterVertex existingUser = usersMap.get(mentionedUser);
        if (existingUser == null) {
          mentionedUser = new TwitterVertex(mentionedScreenName, 1, ApplicationConstants.MENTIONED,
              tweet.getIdStr());
          mentionedUser.setPopularityIndex(1);
          usersMap.put(mentionedUser, mentionedUser);
        } else {
          existingUser.setPopularityIndex(existingUser.getPopularityIndex() + 1);
          usersMap.put(existingUser, existingUser);
        }
      }
    }
  }

  private Set<String> getAllTopicsFromTweet(Tweet tweet, Boolean shouldIgnoreNumbers,
      Boolean areMentionsTopics) {
    Set<String> transaction = new HashSet<>();
    Set<String> structured =
        this.twitterService.getHashtagsAndMentionsFromTweet(tweet, areMentionsTopics);
    Set<String> entities = this.getNamedEntitiesFromText(tweet.getText(), shouldIgnoreNumbers);
    if (!structured.isEmpty()) {
      transaction.addAll(structured);
    }
    if (!entities.isEmpty()) {
      transaction.addAll(entities);
    }
    return transaction;
  }

  private Set<String> getNamedEntitiesFromText(String text, Boolean shouldIgnoreNumbers) {
    return this.naturalLanguageUtils.getEntitiesFromText(text, shouldIgnoreNumbers);
  }

  private Graph<TwitterVertex, TwitterEdge> createGraphFromTopicCommunityList(
      Set<TopicCommunity> communities) {
    Graph<TwitterVertex, TwitterEdge> graph = new DirectedOrderedSparseMultigraph<>();
    for (TopicCommunity topicCommunity : communities) {
      TwitterVertex destination = new TwitterVertex(
          new StringBuilder("#").append(topicCommunity.getCommunityName()).toString());
      for (TwitterVertex user : topicCommunity.getMembers()) {
        graph.addEdge(new TwitterEdge(user, destination), user, destination, EdgeType.DIRECTED);
      }
    }
    return graph;
  }

  private List<Graph<TwitterVertex, TwitterEdge>> createGraphListFormCommunityList(
      Set<TopicCommunity> communities) {
    List<Graph<TwitterVertex, TwitterEdge>> graphs = new ArrayList<>();
    for (TopicCommunity topicCommunity : communities) {
      Graph<TwitterVertex, TwitterEdge> graph = new DirectedOrderedSparseMultigraph<>();
      TwitterVertex destination = new TwitterVertex(
          new StringBuilder("#").append(topicCommunity.getCommunityName()).toString());
      for (TwitterVertex user : topicCommunity.getMembers()) {
        graph.addEdge(new TwitterEdge(user, destination), user, destination, EdgeType.DIRECTED);
      }
      graphs.add(graph);
    }
    return graphs;

  }
}
