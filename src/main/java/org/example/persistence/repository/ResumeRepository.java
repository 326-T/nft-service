package org.example.persistence.repository;

import com.mongodb.lang.Nullable;
import lombok.NonNull;
import org.example.persistence.entity.Resume;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ResumeRepository extends ReactiveMongoRepository<Resume, String> {

  @Query("{}")
  @NonNull
  Flux<Resume> findAll(@Nullable Sort sort);

  @NonNull
  Mono<Resume> findById(@NonNull String id);

  Flux<Resume> findByApplicantId(String applicantId);

  @NonNull
  Mono<Resume> save(@NonNull Resume resume);

  @NonNull
  Mono<Void> deleteById(@NonNull String id);
}
