package com.javeriana.twitter.communitydetection.controller.rest;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;
import com.javeriana.twitter.communitydetection.service.NetworkService;
import com.javeriana.twitter.communitydetection.service.TopicCommunityService;
import com.javeriana.twitter.communitydetection.util.ApplicationConstants;

@RestController
@RequestMapping(value = ApplicationConstants.NETWORK_BASE_URL)
public class NetworkController {

  @Autowired
  private TopicCommunityService communityDetectionService;

  @Autowired
  private NetworkService networkCommunityService;

  @RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Set<TopicCommunity>> getHashtagTweets(SearchParams searchParams) {
    return ResponseEntity.ok(this.communityDetectionService.findCommunitiesByKey(searchParams));

  }

  @RequestMapping(value = "/graph/image/", method = RequestMethod.GET,
      produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> graphNetworkImage(SearchParams searchParams) throws IOException {
    byte[] imageBytes = this.networkCommunityService.getNetworkImageFromTweetParams(searchParams);
    return ResponseEntity.ok(imageBytes);
  }

}
