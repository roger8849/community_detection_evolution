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

@Controller("communityWebController")
@RequestMapping("/")
public class CommunityWebController {

  private ConnectionRepository connectionRepository;

  @Autowired
  private TopicCommunityService topicCommunityService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public String getIndex(Model model) {
    return "index";
  }

  @RequestMapping(value = "topic-community", method = RequestMethod.GET)
  public String getTopicCommunity(Model model) {
    return "community-form";
  }

  @RequestMapping(value = "feed-data-form", method = RequestMethod.GET)
  public String getFeedData(Model model) {
    return "feed-data-form";
  }
  @RequestMapping(value = "girvan-newman-form", method = RequestMethod.GET)
  public String getGirvanNewmanForm(Model model) {
    return "girvan-newman-form";
  }

  @RequestMapping(value = "word-cloud", method = RequestMethod.POST)
  public String wordCloud(SearchParams searchParams, Model model) {
    model.addAttribute("text", searchParams.getText());
    model.addAttribute("support", searchParams.getConfidence());
    model.addAttribute("confidence", searchParams.getConfidence());
    model.addAttribute("shouldIgnoreNumbers", searchParams.getShouldIgnoreNumbers());
    model.addAttribute("areMentionsTopics", searchParams.getAreMentionsTopics());
    model.addAttribute("loadFrom", searchParams.getLoadFrom());
    return "word-cloud";
  }

  @RequestMapping(value = "table-view", method = RequestMethod.POST)
  public String tableView(SearchParams searchParams, Model model) {

    Set<TopicCommunity> communities = this.topicCommunityService
        .findCommunitiesByKey(searchParams);

    model.addAttribute("text", searchParams.getText());
    model.addAttribute("communities", communities);
    return "table-view";
  }

}
