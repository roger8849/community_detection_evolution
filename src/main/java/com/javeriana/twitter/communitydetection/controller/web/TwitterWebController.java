package com.javeriana.twitter.communitydetection.controller.web;

import com.javeriana.twitter.communitydetection.dto.SearchParams;
import com.javeriana.twitter.communitydetection.dto.community.TopicCommunity;
import com.javeriana.twitter.communitydetection.service.TopicCommunityService;
import java.util.Set;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("twitterWebController")
@RequestMapping("/")
public class TwitterWebController {

  private Twitter twitter;

  private ConnectionRepository connectionRepository;

  @Autowired
  private TopicCommunityService topicCommunityService;

  @Inject
  public TwitterWebController(Twitter twitter, ConnectionRepository connectionRepository) {
    this.twitter = twitter;
    this.connectionRepository = connectionRepository;
  }


  @RequestMapping(value = "twitter-connect", method = RequestMethod.GET)
  public String helloTwitter(Model model) {
    if (this.connectionRepository.findPrimaryConnection(Twitter.class) == null) {
      return "redirect:/connect/twitter";
    }

    model.addAttribute(this.twitter.userOperations().getUserProfile());
    CursoredList<TwitterProfile> friends = this.twitter.friendOperations().getFriends();
    model.addAttribute("friends", friends);
    return "hello";
  }
}
