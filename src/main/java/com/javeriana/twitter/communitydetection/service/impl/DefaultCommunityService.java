package com.javeriana.twitter.communitydetection.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Service;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterEdge;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;
import com.javeriana.twitter.communitydetection.service.CommunityService;
import com.javeriana.twitter.communitydetection.service.NetworkService;
import com.javeriana.twitter.communitydetection.service.TwitterService;
import com.javeriana.twitter.communitydetection.util.GraphUtils;
import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

@Service
public class DefaultCommunityService implements CommunityService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultNetworkService.class);

  @Autowired
  private TwitterService twitterService;

  @Autowired
  private NetworkService networkService;

  /*
   * (non-Javadoc)
   *
   * @see com.javeriana.twitter.communitydetection.service.impl.CommunityService#
   * getCommunityImageFromTweetKey(java.lang.String)
   */
  @Override
  public byte[] getCommunityImageFromTweetParams(SearchParams searchParams) throws IOException {
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    return this.generateCommunityImageUsingTweetList(tweets);
  }

  @Override
  public Set<Set<TwitterVertex>> getBetweennessCommunity(SearchParams searchParams) {
    List<Tweet> tweets = this.twitterService.getTweetsBySearchParams(searchParams);
    return this.getSetOfCommunitiesFromTweetList(tweets);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.javeriana.twitter.communitydetection.service.impl.CommunityService#
   * generateCommunityImageUsingTweetList(java.util.List)
   */
  @Override
  public byte[] generateCommunityImageUsingTweetList(List<Tweet> tweets) throws IOException {
    List<Graph<TwitterVertex, TwitterEdge>> graphs = this.getCommunityGraphFromTweetList(tweets);
    GraphUtils.createCommunityImagesFromGraphList(graphs);
    Graph<TwitterVertex, TwitterEdge> graph = this.getCommunityFullGraphFromTweetList(tweets);
    return GraphUtils.getImageBytesFromGraph(graph);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * com.javeriana.twitter.communitydetection.service.impl.CommunityService#getGraphFromTweetList(
   * java.util.List)
   */
  @Override
  public List<Graph<TwitterVertex, TwitterEdge>> getCommunityGraphFromTweetList(List<Tweet> tweets)
      throws IOException {
    List<Graph<TwitterVertex, TwitterEdge>> communityGraphs = new ArrayList<>();
    Set<Set<TwitterVertex>> communities = this.getSetOfCommunitiesFromTweetList(tweets);

    Graph<TwitterVertex, TwitterEdge> graph = null;
    for (Set<TwitterVertex> users : communities) {
      graph = new UndirectedSparseMultigraph<>();
      for (TwitterVertex user : users) {
        graph.addVertex(user);
      }
      communityGraphs.add(graph);
    }

    return communityGraphs;
  }

  @Override
  public Graph<TwitterVertex, TwitterEdge> getCommunityFullGraphFromTweetList(List<Tweet> tweets)
      throws IOException {
    Graph<TwitterVertex, TwitterEdge> communityGraph = new UndirectedOrderedSparseMultigraph<>();
    Set<Set<TwitterVertex>> communities = this.getSetOfCommunitiesFromTweetList(tweets);

    for (Set<TwitterVertex> users : communities) {
      List<TwitterVertex> usersList = new ArrayList<>(users);
      if (usersList.size() == 1) {
        communityGraph.addVertex(usersList.get(0));
      } else {
        for (int i = 0; i < usersList.size() - 1; i++) {
          TwitterVertex source = usersList.get(i);
          for (int j = i + 1; j < usersList.size(); j++) {
            TwitterVertex destination = usersList.get(j);
            communityGraph.addEdge(new TwitterEdge(source, destination), source, destination,
                EdgeType.UNDIRECTED);
          }
        }
      }
    }
    return communityGraph;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.javeriana.twitter.communitydetection.service.impl.CommunityService#
   * getSetOfCommunitiesFromTweetList(java.util.List)
   */
  @Override
  public Set<Set<TwitterVertex>> getSetOfCommunitiesFromTweetList(List<Tweet> tweets) {
    LOG.debug("Finding communities by edge betweenness");
    Graph<TwitterVertex, TwitterEdge> networkGraph =
        this.networkService.getGraphFromTweetList(tweets);
    EdgeBetweennessClusterer<TwitterVertex, TwitterEdge> clusterer =
        new EdgeBetweennessClusterer<>(1);
    Set<Set<TwitterVertex>> clusters = clusterer.apply(networkGraph);
    LOG.debug("Ending communities by edge betweenness");
    return clusters;
  }

}
