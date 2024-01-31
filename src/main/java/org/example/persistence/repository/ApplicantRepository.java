package org.example.persistence.repository;

import com.mongodb.lang.Nullable;
import lombok.NonNull;
import org.example.persistence.entity.Applicant;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ApplicantRepository extends ReactiveMongoRepository<Applicant, String> {

  @Query("{}")
  @NonNull
  Flux<Applicant> findAll(@Nullable Sort sort);

  @NonNull
  Mono<Applicant> findById(@NonNull String id);

  Mono<Applicant> findByEmail(@NonNull String email);

  @NonNull
  Mono<Applicant> save(@NonNull Applicant applicant);

  @NonNull
  Mono<Void> deleteById(@NonNull String id);
}
