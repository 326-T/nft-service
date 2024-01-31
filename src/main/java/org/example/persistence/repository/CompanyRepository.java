package org.example.persistence.repository;

import com.mongodb.lang.Nullable;
import lombok.NonNull;
import org.example.persistence.entity.Company;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CompanyRepository extends ReactiveMongoRepository<Company, String> {

  @Query("{}")
  @NonNull
  Flux<Company> findAll(@Nullable Sort sort);

  @NonNull
  Mono<Company> findById(@NonNull String id);

  Mono<Company> findByEmail(@NonNull String email);

  @NonNull
  Mono<Company> save(@NonNull Company applicant);

  @NonNull
  Mono<Void> deleteById(@NonNull String id);
}
