package org.example.persistence.repository;

import java.util.UUID;
import lombok.NonNull;
import org.example.persistence.entity.Offer;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OfferRepository extends ReactiveCrudRepository<Offer, String> {

  @NonNull
  Flux<Offer> findAll(Sort sort);

  @NonNull
  Mono<Offer> findByUuid(@NonNull UUID id);

  Flux<Offer> findByResumeUuid(UUID resumeUuid);

  @NonNull
  Mono<Offer> save(@NonNull Offer offer);

  @NonNull
  Mono<Void> deleteByUuid(@NonNull UUID id);
}
