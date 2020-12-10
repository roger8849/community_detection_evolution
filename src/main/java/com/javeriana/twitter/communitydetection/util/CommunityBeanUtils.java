package com.javeriana.twitter.communitydetection.util;

import com.javeriana.twitter.communitydetection.dto.tweet.CustomTweet;
import org.springframework.social.twitter.api.Entities;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;

public class CommunityBeanUtils {

  /**
   * CUSTOM TWEET TO TWEET METHODS
   */
  public static Tweet mapTweetFromCustomTweet(CustomTweet customTweet) {

    Tweet tweet = new Tweet(customTweet.getId(), customTweet.getIdStr(), customTweet.getText(),
        customTweet.getCreatedAt(), customTweet.getFromUser(), customTweet.getProfileImageUrl(),
        customTweet.getToUserId(), customTweet.getToUserId(), customTweet.getLanguageCode(),
        customTweet.getSource());
    tweet.setInReplyToScreenName(customTweet.getInReplyToScreenName());
    tweet.setInReplyToStatusId(customTweet.getInReplyToStatusId());
    tweet.setInReplyToUserId(customTweet.getInReplyToUserId());
    tweet.setRetweetCount(customTweet.getRetweetCount());
    tweet.setRetweeted(customTweet.isRetweeted());
    if (customTweet.getRetweetedStatus() != null) {
      tweet.setRetweetedStatus(mapTweetFromCustomTweet(customTweet.getRetweetedStatus()));
    }
    tweet.setFavorited(tweet.isFavorited());
    tweet.setFavoriteCount(customTweet.getFavoriteCount());
    tweet.setEntities(CommunityBeanUtils.mapEntitiesFromCustomEntities(customTweet.getEntities()));
    tweet.setUser(
        CommunityBeanUtils.mapTwitterProfileFromCustomTwitterProfile(customTweet.getUser()));
    return tweet;
  }

  public static Entities mapEntitiesFromCustomEntities(
      com.javeriana.twitter.communitydetection.dto.tweet.Entities customEntities) {
    Entities entities = new Entities(customEntities.getUrls(), customEntities.getHashTags(),
        customEntities.getMentions(), customEntities.getMedia(), customEntities.getTickerSymbols());
    return entities;
  }



  public static TwitterProfile mapTwitterProfileFromCustomTwitterProfile(
      com.javeriana.twitter.communitydetection.dto.tweet.TwitterProfile customTwitterProfile) {
    TwitterProfile twitterProfile = new TwitterProfile(customTwitterProfile.getId(),
        customTwitterProfile.getScreenName(), customTwitterProfile.getName(),
        customTwitterProfile.getUrl(), customTwitterProfile.getProfileImageUrl(),
        customTwitterProfile.getDescription(), customTwitterProfile.getLocation(),
        customTwitterProfile.getCreatedDate());
    return twitterProfile;
  }

  /*
   * TWEET TO CUSTOM TWEET METHODS
   */

  public static CustomTweet mapCustomTweetFromTweet(Tweet tweet) {

    CustomTweet customTweet = new CustomTweet(tweet.getId(), tweet.getIdStr(), tweet.getText(),
        tweet.getCreatedAt(), tweet.getFromUser(), tweet.getProfileImageUrl(),
        tweet.getToUserId(), tweet.getToUserId(), tweet.getLanguageCode(),
        tweet.getSource());
    customTweet.setInReplyToScreenName(tweet.getInReplyToScreenName());
    customTweet.setInReplyToStatusId(tweet.getInReplyToStatusId());
    customTweet.setInReplyToUserId(tweet.getInReplyToUserId());
    customTweet.setRetweetCount(tweet.getRetweetCount());
    customTweet.setRetweeted(tweet.isRetweeted());
    if (tweet.getRetweetedStatus() != null) {
      customTweet.setRetweetedStatus(mapCustomTweetFromTweet(tweet.getRetweetedStatus()));
    }
    customTweet.setFavorited(tweet.isFavorited());
    customTweet.setFavoriteCount(tweet.getFavoriteCount());
    customTweet.setEntities(CommunityBeanUtils.mapCustomEntitesFromEntities(tweet.getEntities()));
    customTweet.setUser(
        CommunityBeanUtils.mapCustomTwitterProfieFromTwitterProfile(tweet.getUser()));
    return customTweet;
  }

  public static com.javeriana.twitter.communitydetection.dto.tweet.Entities mapCustomEntitesFromEntities(
      Entities entities) {
    com.javeriana.twitter.communitydetection.dto.tweet.Entities customEntities = new com.javeriana.twitter.communitydetection.dto.tweet.Entities(
        entities.getUrls(), entities.getHashTags(), entities.getMentions(), entities.getMedia(),
        entities.getTickerSymbols());
    return customEntities;
  }

  public static com.javeriana.twitter.communitydetection.dto.tweet.TwitterProfile mapCustomTwitterProfieFromTwitterProfile(TwitterProfile twitterProfile){
    com.javeriana.twitter.communitydetection.dto.tweet.TwitterProfile customTwitterProfile = new com.javeriana.twitter.communitydetection.dto.tweet.TwitterProfile(twitterProfile.getId(),
        twitterProfile.getScreenName(), twitterProfile.getName(),
        twitterProfile.getUrl(), twitterProfile.getProfileImageUrl(),
        twitterProfile.getDescription(), twitterProfile.getLocation(),
        twitterProfile.getCreatedDate());
    customTwitterProfile.setLanguage(twitterProfile.getLanguage());
    customTwitterProfile.setStatusesCount(twitterProfile.getStatusesCount());
    customTwitterProfile.setFriendsCount(twitterProfile.getFriendsCount());
    customTwitterProfile.setFavoritesCount(twitterProfile.getFavoritesCount());
    customTwitterProfile.setListedCount(twitterProfile.getListedCount());
    customTwitterProfile.setFollowing(twitterProfile.isFollowing());
    customTwitterProfile.setFollowRequestSent(twitterProfile.isProtected());
    customTwitterProfile.setNotificationsEnabled(twitterProfile.isNotificationsEnabled());
    customTwitterProfile.setVerified(twitterProfile.isVerified());
    customTwitterProfile.setGeoEnabled(twitterProfile.isGeoEnabled());
    customTwitterProfile.setContributorsEnabled(twitterProfile.isContributorsEnabled());
    customTwitterProfile.setTranslator(twitterProfile.isTranslator());
    customTwitterProfile.setTimeZone(twitterProfile.getTimeZone());
    customTwitterProfile.setUtcOffset(twitterProfile.getUtcOffset());
    customTwitterProfile.setSidebarBorderColor(twitterProfile.getSidebarBorderColor());
    customTwitterProfile.setSidebarFillColor(twitterProfile.getSidebarFillColor());
    customTwitterProfile.setBackgroundColor(twitterProfile.getBackgroundColor());
    customTwitterProfile.setUseBackgroundImage(twitterProfile.useBackgroundImage());
    customTwitterProfile.setBackgroundImageUrl(twitterProfile.getBackgroundImageUrl());
    customTwitterProfile.setBackgroundImageTiled(twitterProfile.isBackgroundImageTiled());
    customTwitterProfile.setTextColor(twitterProfile.getTextColor());
    customTwitterProfile.setLinkColor(twitterProfile.getLinkColor());
    customTwitterProfile.setShowAllInlineMedia(twitterProfile.showAllInlineMedia());
    customTwitterProfile.setProfileBannerUrl(twitterProfile.getProfileBannerUrl());
    return customTwitterProfile;
  }
}
