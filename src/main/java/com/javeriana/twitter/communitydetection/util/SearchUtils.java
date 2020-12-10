package com.javeriana.twitter.communitydetection.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.GeoCode;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchParameters.ResultType;
import org.springframework.stereotype.Component;

import com.javeriana.twitter.communitydetection.dto.SearchParams;

@Component
public class SearchUtils {

  @Autowired
  private AppProperties appProperties;

  public SearchParameters getDefaultSearchParameters(SearchParams searchParams) {
    String query = searchParams.getText();
    Integer count = searchParams.getCount() == null ? this.appProperties.getSearchCount()
        : searchParams.getCount();

    return new SearchParameters(query).geoCode(this.getColombiaGeocode())
        .resultType(ResultType.RECENT).count(count);
  }

  public GeoCode getColombiaGeocode() {
    return new GeoCode(this.appProperties.getGeocode().getLatitude(),
        this.appProperties.getGeocode().getLongitude(),
        this.appProperties.getGeocode().getRadius());

  }
}
