package com.javeriana.twitter.communitydetection.service;

import java.io.IOException;
import java.util.List;

import org.springframework.social.twitter.api.Tweet;

import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;

import edu.uci.ics.jung.graph.Graph;

public interface NetworkService {

  byte[] generateNetworkImageUsingTweetList(List<Tweet> tweets) throws IOException;

  Graph<TwitterVertex, TwitterEdge> getGraphFromTweetList(List<Tweet> tweets);

  byte[] getNetworkImageFromTweetParams(SearchParams searchParams) throws IOException;

}
