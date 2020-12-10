package com.javeriana.twitter.communitydetection.repository;

import com.javeriana.twitter.communitydetection.dto.wrapper.TweetWrapper;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TweetWrapperRepository extends MongoRepository<TweetWrapper, UUID> {
  List<TweetWrapper> getAllByProcessed(boolean processed);

}
