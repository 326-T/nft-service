package org.example.persistence.repository;

import java.util.UUID;
import lombok.NonNull;
import org.example.persistence.entity.Resume;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResumeRepository extends ReactiveCrudRepository<Resume, String> {

  @NonNull
  Flux<Resume> findAll(Sort sort);

  @NonNull
  Mono<Resume> findByUuid(@NonNull UUID id);

  Flux<Resume> findByApplicantId(UUID applicantId);

  @NonNull
  Mono<Resume> save(@NonNull Resume resume);

  @NonNull
  Mono<Void> deleteById(@NonNull UUID id);
}
