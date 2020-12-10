package com.javeriana.twitter.communitydetection.repository;

import com.javeriana.twitter.communitydetection.dto.wrapper.CommunityWrapper;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityWrapperRepository extends MongoRepository<CommunityWrapper, UUID> {
  CommunityWrapper findFirstByOrderByProcessedAtDesc();
  CommunityWrapper findFirstByOrderByProcessedAtAsc();
}
