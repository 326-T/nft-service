package org.example.persistence.repository;

import java.util.UUID;
import lombok.NonNull;
import org.example.persistence.entity.Applicant;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ApplicantRepository extends ReactiveCrudRepository<Applicant, String> {

  @NonNull
  Flux<Applicant> findAll(Sort sort);

  @NonNull
  Mono<Applicant> findByUuid(@NonNull UUID id);

  Mono<Applicant> findByEmail(@NonNull String email);

  @NonNull
  Mono<Applicant> save(@NonNull Applicant applicant);

  @NonNull
  Mono<Void> deleteById(@NonNull UUID id);
}
