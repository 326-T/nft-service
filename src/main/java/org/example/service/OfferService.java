package org.example.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.constant.OfferStatus;
import org.example.error.exception.NotFoundException;
import org.example.persistence.entity.Offer;
import org.example.persistence.repository.OfferRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OfferService {

  private final OfferRepository offerRepository;

  public OfferService(OfferRepository offerRepository) {
    this.offerRepository = offerRepository;
  }

  public Flux<Offer> findAll() {
    return offerRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Offer> findByUuid(UUID id) {
    return offerRepository.findByUuid(id);
  }

  public Mono<Offer> save(Offer offer) {
    return offerRepository.save(offer);
  }

  public Mono<Offer> update(Offer offer) {
    return offerRepository.findByUuid(offer.getUuid())
        .switchIfEmpty(Mono.error(new NotFoundException("Offer not found.")))
        .map(old -> Offer.builder()
            .uuid(old.getUuid())
            .resumeUuid(old.getResumeUuid())
            .companyUuid(old.getCompanyUuid())
            .price(offer.getPrice())
            .message(offer.getMessage())
            .statusId(offer.getStatusId())
            .createdAt(old.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build())
        .flatMap(offerRepository::save);
  }

  public Mono<Offer> updateOnlyStatus(UUID uuid, OfferStatus status) {
    return offerRepository.findByUuid(uuid)
        .switchIfEmpty(Mono.error(new NotFoundException("Offer not found.")))
        .map(old -> Offer.builder()
            .uuid(old.getUuid())
            .resumeUuid(old.getResumeUuid())
            .companyUuid(old.getCompanyUuid())
            .price(old.getPrice())
            .message(old.getMessage())
            .statusId(status.getId())
            .createdAt(old.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build())
        .flatMap(offerRepository::save);
  }

  public Mono<Void> deleteById(UUID id) {
    return offerRepository.deleteByUuid(id);
  }
}
