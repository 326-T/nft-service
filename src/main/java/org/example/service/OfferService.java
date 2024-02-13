package org.example.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.example.constant.OfferStatus;
import org.example.error.exception.NotFoundException;
import org.example.persistence.dto.OfferDetailView;
import org.example.persistence.entity.Offer;
import org.example.persistence.repository.OfferDetailViewRepository;
import org.example.persistence.repository.OfferRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class OfferService {

  private final OfferRepository offerRepository;
  private final OfferDetailViewRepository offerDetailViewRepository;

  public OfferService(OfferRepository offerRepository,
      OfferDetailViewRepository offerDetailViewRepository) {
    this.offerRepository = offerRepository;
    this.offerDetailViewRepository = offerDetailViewRepository;
  }

  public Flux<Offer> findAll() {
    return offerRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Offer> findByUuid(UUID id) {
    return offerRepository.findByUuid(id);
  }

  public Flux<OfferDetailView> findByResumeUuid(UUID resumeUuid) {
    return offerDetailViewRepository.findByResumeUuid(resumeUuid);
  }

  public Mono<Offer> save(Offer offer) {
    return offerRepository.save(offer);
  }

  public Mono<Offer> update(Offer offer) {
    return offerRepository.findByUuid(offer.getUuid())
        .switchIfEmpty(Mono.error(new NotFoundException("Offer not found.")))
        .filter(old -> Objects.equals(old.getStatusId(), OfferStatus.PENDING.getId()))
        .switchIfEmpty(Mono.error(new NotFoundException("Offer status is not pending.")))
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

  public Mono<Void> rejectCheaper(UUID resumeUuid, UUID offerUuid) {
    Mono<Offer> border = offerRepository.findByUuid(offerUuid);
    return offerDetailViewRepository.findByResumeUuid(resumeUuid)
        .filter(offer -> Objects.equals(offer.getStatusId(), OfferStatus.PENDING.getId()))
        .filter(offer -> !Objects.equals(offer.getUuid(), offerUuid))
        .filterWhen(offer -> border.map(borderOffer -> borderOffer.getPrice() > offer.getPrice()))
        .flatMap(offer -> updateOnlyStatus(offer.getUuid(), OfferStatus.REJECTED))
        .then();
  }

  public Mono<Offer> updateOnlyStatus(UUID uuid, OfferStatus status) {
    return offerRepository.findByUuid(uuid)
        .switchIfEmpty(Mono.error(new NotFoundException("Offer not found.")))
        .filter(old -> Objects.equals(old.getStatusId(), OfferStatus.PENDING.getId()))
        .switchIfEmpty(Mono.error(new NotFoundException("Offer status is not pending.")))
        .map(old -> Offer.builder()
            .id(old.getId())
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
