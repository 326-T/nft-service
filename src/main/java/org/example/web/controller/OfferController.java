package org.example.web.controller;

import java.util.UUID;
import org.example.constant.ContextKeys;
import org.example.constant.OfferStatus;
import org.example.persistence.entity.Company;
import org.example.persistence.entity.Offer;
import org.example.service.OfferService;
import org.example.service.ReactiveContextService;
import org.example.web.request.OfferRequest;
import org.example.web.response.OfferDetailResponse;
import org.example.web.response.OfferResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/offers")
public class OfferController {

  private final OfferService offerService;
  private final ReactiveContextService reactiveContextService;

  public OfferController(OfferService offerService, ReactiveContextService reactiveContextService) {
    this.offerService = offerService;
    this.reactiveContextService = reactiveContextService;
  }

  @GetMapping
  public Flux<OfferResponse> index() {
    return offerService.findAll().map(OfferResponse::new);
  }

  @GetMapping("/{id}")
  public Mono<OfferResponse> findByUuid(@PathVariable UUID id) {
    return offerService.findByUuid(id).map(OfferResponse::new);
  }

  @GetMapping("/resume/{id}")
  public Flux<OfferDetailResponse> findByResumeUuid(@PathVariable UUID id) {
    return offerService.findByResumeUuid(id).map(OfferDetailResponse::new);
  }

  @PostMapping
  public Mono<OfferResponse> save(ServerWebExchange exchange,
      @RequestBody OfferRequest request) {
    Offer offer = request.exportEntity();
    Company company = reactiveContextService.getAttribute(exchange, ContextKeys.COMPANY_KEY);
    offer.setCompanyUuid(company.getUuid());
    return offerService.save(offer).map(OfferResponse::new);
  }

  @PatchMapping("/{id}")
  public Mono<OfferResponse> update(@PathVariable UUID id,
      @RequestBody OfferRequest request) {
    Offer offer = request.exportEntity();
    offer.setUuid(id);
    return offerService.update(offer).map(OfferResponse::new);
  }

  @PatchMapping("/accepted/{id}")
  public Mono<OfferResponse> accepted(@PathVariable UUID id) {
    return offerService.updateOnlyStatus(id, OfferStatus.ACCEPTED).map(OfferResponse::new);
  }

  @PatchMapping("/rejected/{id}")
  public Mono<OfferResponse> rejected(@PathVariable UUID id) {
    return offerService.updateOnlyStatus(id, OfferStatus.REJECTED).map(OfferResponse::new);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return offerService.deleteById(id);
  }
}
