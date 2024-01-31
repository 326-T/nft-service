package org.example.persistence.repository;

import java.util.UUID;
import lombok.NonNull;
import org.example.persistence.entity.Company;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CompanyRepository extends ReactiveCrudRepository<Company, String> {

  @NonNull
  Flux<Company> findAll(Sort sort);

  @NonNull
  Mono<Company> findByUuid(@NonNull UUID id);

  Mono<Company> findByEmail(@NonNull String email);

  @NonNull
  Mono<Company> save(@NonNull Company applicant);

  @NonNull
  Mono<Void> deleteByUuid(@NonNull UUID id);
}
