package com.javeriana.twitter.communitydetection.controller.rest;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.service.TwitterService;
import com.javeriana.twitter.communitydetection.util.ApplicationConstants;

@RestController
@RequestMapping(value = ApplicationConstants.TWITTER_BASE_URL)
public class TwitterController {

  @Autowired
  private TwitterService twitterService;

  @RequestMapping(value = "", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Tweet> getHashtagTweets(SearchParams searchParams) {
    return this.twitterService.getTweetsBySearchParams(searchParams);
  }

  @RequestMapping(value = "hometimeline", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<Tweet>> getHomeTimeline() {
    List<Tweet> tweets = this.twitterService.getHomeTimelineTweets();
    return ResponseEntity.ok(tweets);
  }

  @RequestMapping(value = "save", method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<Tweet> saveTweetInMongo(@RequestParam("tweet-id") String tweetId) {
    return ResponseEntity.ok(this.twitterService.saveTweetInMongoDatabase(tweetId));
  }

  @RequestMapping(value = "feed-data/", method = RequestMethod.POST)
  public void feedData(@RequestParam("text") String text) {
    this.twitterService.streamTweetsInDatabase(text);
  }

  @RequestMapping(value = "stop-feed", method = RequestMethod.POST)
  public void stopfeed() {
    this.twitterService.stopTwitterStreaming();
  }
}
