package org.example.persistence.repository;

import java.util.UUID;
import lombok.NonNull;
import org.example.persistence.dto.OfferDetailView;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OfferDetailViewRepository extends ReactiveCrudRepository<OfferDetailView, String> {

  @NonNull
  Flux<OfferDetailView> findByResumeUuid(UUID resumeUuid);
}
