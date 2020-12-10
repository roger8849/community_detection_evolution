package com.javeriana.twitter.communitydetection.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.MentionEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.UrlEntity;
import org.springframework.stereotype.Service;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;
import com.javeriana.twitter.communitydetection.service.NetworkService;
import com.javeriana.twitter.communitydetection.service.TwitterService;
import com.javeriana.twitter.communitydetection.util.GraphUtils;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

@Service(DefaultNetworkService.STRUCTURE_COMMUNITY)
public class DefaultNetworkService implements NetworkService {



  private static final Logger LOG = LoggerFactory.getLogger(DefaultNetworkService.class);

  private static final EdgeType DIRECTED = EdgeType.DIRECTED;

  public static final String STRUCTURE_COMMUNITY = "structureCommunity";

  @Autowired
  private TwitterService twitterService;

  @Override
  public byte[] getNetworkImageFromTweetParams(SearchParams searchParams) throws IOException {
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    return this.generateNetworkImageUsingTweetList(tweets);
  }

  @Override
  public byte[] generateNetworkImageUsingTweetList(List<Tweet> tweets) throws IOException {
    Graph<TwitterVertex, TwitterEdge> graph = this.getGraphFromTweetList(tweets);
    return GraphUtils.getImageBytesFromGraph(graph);
  }

  @Override
  public Graph<TwitterVertex, TwitterEdge> getGraphFromTweetList(List<Tweet> tweets) {
    Graph<TwitterVertex, TwitterEdge> graph = new DirectedSparseMultigraph<>();

    graph = this.setEdgesForMentions(tweets, graph);

    graph = this.setEdgesForRetweets(tweets, graph);

    graph = this.setEdgesForQuoteTweets(tweets, graph);

    graph = this.setEdgesForReplies(tweets, graph);

    // Set<TwitterVertex> users = tweets.stream()
    // .map(t -> new TwitterVertex(t.getUser().getScreenName())).collect(Collectors.toSet());
    // for (TwitterVertex user : users) {
    // if (!graph.containsVertex(user)) {
    // graph.addVertex(user);
    // }
    // }

    return graph;
  }

  private Graph<TwitterVertex, TwitterEdge> setEdgesForMentions(List<Tweet> tweets,
      Graph<TwitterVertex, TwitterEdge> graph) {
    LOG.debug("Setting edges for mentions.");
    List<MentionEntity> mentions = new ArrayList<>();
    for (Tweet tweet : tweets) {
      mentions = tweet.getEntities().getMentions();
      if (mentions != null && !mentions.isEmpty()) {
        for (MentionEntity mention : mentions) {
          TwitterVertex source = new TwitterVertex(tweet.getFromUser());
          TwitterVertex destination = new TwitterVertex(mention.getScreenName());
          graph = this.addTwitterEdgeToGraph(graph, source, destination);
        }
      }

    }
    LOG.debug("End Setting edges for mentions.");
    return graph;
  }

  private Graph<TwitterVertex, TwitterEdge> setEdgesForRetweets(List<Tweet> tweets,
      Graph<TwitterVertex, TwitterEdge> graph) {
    LOG.debug("Setting edges for retweets.");
    List<Tweet> retweets =
        tweets.stream().filter(t -> null != t.getRetweetedStatus()).collect(Collectors.toList());
    for (Tweet retweet : retweets) {
      TwitterVertex source = new TwitterVertex(retweet.getFromUser());
      TwitterVertex destination = new TwitterVertex(retweet.getRetweetedStatus().getFromUser());
      this.addTwitterEdgeToGraph(graph, source, destination);
    }
    LOG.debug("End setting edges for retweets.");
    return graph;
  }

  private Graph<TwitterVertex, TwitterEdge> setEdgesForQuoteTweets(List<Tweet> tweets,
      Graph<TwitterVertex, TwitterEdge> graph) {
    LOG.debug("Setting edges for quote tweets.");
    List<Tweet> probablyQuoteTweets = tweets.stream()
        .filter(t -> null != t.getEntities().getUrls() && !t.getEntities().getUrls().isEmpty())
        .collect(Collectors.toList());
    for (Tweet quoteTweet : probablyQuoteTweets) {
      List<UrlEntity> urlEntities = quoteTweet.getEntities().getUrls();
      TwitterVertex source = new TwitterVertex(quoteTweet.getFromUser());
      for (UrlEntity urlEntity : urlEntities) {
        String tweetId = this.twitterService.getTweetIdFromURLEntity(urlEntity);
        if (null != tweetId) {
          TwitterVertex destination =
              new TwitterVertex(this.twitterService.getTweetByTweetId(tweetId).getFromUser());
          this.addTwitterEdgeToGraph(graph, source, destination);
        }
      }
    }
    LOG.debug("End setting edges for quote tweets.");
    return graph;
  }

  private Graph<TwitterVertex, TwitterEdge> setEdgesForReplies(List<Tweet> tweets,
      Graph<TwitterVertex, TwitterEdge> graph) {
    LOG.debug("Setting edges for replies.");
    List<Tweet> probablyQuoteTweets =
        tweets.stream().filter(t -> null != t.getInReplyToUserId()).collect(Collectors.toList());
    for (Tweet tweet : probablyQuoteTweets) {
      TwitterVertex source = new TwitterVertex(tweet.getFromUser());
      TwitterVertex destination = new TwitterVertex(tweet.getInReplyToScreenName());
      this.addTwitterEdgeToGraph(graph, source, destination);
    }
    LOG.debug("End setting edges for replies.");
    return graph;
  }

  private Graph<TwitterVertex, TwitterEdge> addTwitterEdgeToGraph(
      Graph<TwitterVertex, TwitterEdge> graph, TwitterVertex source, TwitterVertex destination) {
    TwitterEdge edge = new TwitterEdge(source, destination);

    // if (source.getScreenName().equalsIgnoreCase("roger8849")) {
    // LOG.debug("@roger8849 tweet");
    // }

    if (graph.containsEdge(edge)) {
      edge = graph.findEdge(source, destination);
      int weight = edge.getWeight() + 1;
      graph.removeEdge(edge);
      edge.setWeight(weight);
      graph.addEdge(edge, source, destination, DIRECTED);
    } else {
      graph.addEdge(new TwitterEdge(source, destination), source, destination, DIRECTED);
    }

    return graph;
  }



}
