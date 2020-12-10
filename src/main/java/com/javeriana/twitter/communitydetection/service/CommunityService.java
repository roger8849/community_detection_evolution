package com.javeriana.twitter.communitydetection.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.social.twitter.api.Tweet;

import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;

import edu.uci.ics.jung.graph.Graph;

public interface CommunityService {

  byte[] generateCommunityImageUsingTweetList(List<Tweet> tweets) throws IOException;

  List<Graph<TwitterVertex, TwitterEdge>> getCommunityGraphFromTweetList(List<Tweet> tweets)
      throws IOException;

  Set<Set<TwitterVertex>> getSetOfCommunitiesFromTweetList(List<Tweet> tweets);

  Graph<TwitterVertex, TwitterEdge> getCommunityFullGraphFromTweetList(List<Tweet> tweets)
      throws IOException;

  Set<Set<TwitterVertex>> getBetweennessCommunity(SearchParams searchParams);

  byte[] getCommunityImageFromTweetParams(SearchParams searchParams) throws IOException;

}
