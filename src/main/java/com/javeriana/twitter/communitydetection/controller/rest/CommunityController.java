package com.javeriana.twitter.communitydetection.controller.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;
import com.javeriana.twitter.communitydetection.dto.graph.TwitterVertex;
import com.javeriana.twitter.communitydetection.service.CommunityService;
import com.javeriana.twitter.communitydetection.service.TopicCommunityService;
import com.javeriana.twitter.communitydetection.util.ApplicationConstants;
import com.javeriana.twitter.communitydetection.util.NaturalLanguageUtils;

@RestController
@RequestMapping(value = ApplicationConstants.COMMUNITY_BASE_URL)
public class CommunityController {

  @Autowired
  private CommunityService communityService;

  @Autowired
  private TopicCommunityService topicCommunityService;

  @Autowired
  private NaturalLanguageUtils naturalLanguageUtils;

  @RequestMapping(value = "/betweenness/", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Set<Set<TwitterVertex>>> betweennessCommunity(
      @Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.communityService.getBetweennessCommunity(searchParams));
  }

  @RequestMapping(value = "/betweenness/image/", method = RequestMethod.GET,
      produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> betweennessCommunityImage(@Valid SearchParams searchParams)
      throws IOException {
    byte[] imageBytes = this.communityService.getCommunityImageFromTweetParams(searchParams);
    return ResponseEntity.ok(imageBytes);
  }

  @RequestMapping(value = "/topic/", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Set<TopicCommunity>> topicCommunity(@Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.topicCommunityService.findCommunitiesByKey(searchParams));
  }

  @RequestMapping(value = "/topic/image/", method = RequestMethod.GET,
      produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> topicCommunityImage(@Valid SearchParams searchParams)
      throws IOException {
    byte[] imageBytes = this.topicCommunityService.findCommunityImageByKey(searchParams);
    return ResponseEntity.ok(imageBytes);
  }

  @RequestMapping(value = "/topic/associationrules/", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<FPGrowthModel<String>> associationRulesByKey(
      @Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.topicCommunityService.associationRulesByKey(searchParams));
  }

  @RequestMapping(value = "/topic/entities", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<Set<String>>> entitiesTweets(@Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.topicCommunityService.getNamedEntitiesFromTweets(searchParams));
  }

  @RequestMapping(value = "/topic/all", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<Set<String>>> allTopicsTweets(@Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.topicCommunityService.getAllTopicsFromTweets(searchParams));
  }


  @RequestMapping(value = "/topic/text/entities", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Set<String>> entitiesFromText(@Valid SearchParams searchParams) {
    return ResponseEntity.ok(this.naturalLanguageUtils.getEntitiesFromText(searchParams.getText(),
        searchParams.getShouldIgnoreNumbers()));
  }

}
