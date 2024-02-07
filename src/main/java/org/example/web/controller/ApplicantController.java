package org.example.web.controller;

import java.util.UUID;
import org.example.constant.ContextKeys;
import org.example.constant.CookieKeys;
import org.example.persistence.entity.Applicant;
import org.example.service.ApplicantService;
import org.example.service.Base64Service;
import org.example.service.JwtService;
import org.example.service.ReactiveContextService;
import org.example.web.request.ApplicantInsertRequest;
import org.example.web.request.ApplicantLoginRequest;
import org.example.web.response.ApplicantResponse;
import org.springframework.http.ResponseCookie;
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
@RequestMapping("/api/v1/applicants")
public class ApplicantController {

  private final ApplicantService applicantService;
  private final JwtService jwtService;
  private final Base64Service base64Service;
  private final ReactiveContextService reactiveContextService;

  public ApplicantController(ApplicantService applicantService, JwtService jwtService,
      Base64Service base64Service, ReactiveContextService reactiveContextService) {
    this.applicantService = applicantService;
    this.jwtService = jwtService;
    this.base64Service = base64Service;
    this.reactiveContextService = reactiveContextService;
  }

  @GetMapping
  public Flux<ApplicantResponse> index() {
    return applicantService.findAll().map(ApplicantResponse::new);
  }

  @GetMapping("/{id}")
  public Mono<ApplicantResponse> findByUuid(@PathVariable UUID id) {
    return applicantService.findByUuid(id).map(ApplicantResponse::new);
  }

  @GetMapping("/current")
  public Mono<ApplicantResponse> current(ServerWebExchange exchange) {
    Applicant applicant = reactiveContextService.getAttribute(exchange, ContextKeys.APPLICANT_KEY);
    return Mono.just(applicant).map(ApplicantResponse::new);
  }

  @PostMapping
  public Mono<Void> save(ServerWebExchange exchange, @RequestBody ApplicantInsertRequest request) {
    return applicantService.save(request.exportEntity(), request.getPassword())
        .map(jwtService::encodeApplicant)
        .map(base64Service::encode)
        .doOnNext(jwt -> exchange.getResponse().addCookie(
            ResponseCookie
                .from(CookieKeys.APPLICANT_TOKEN, jwt)
                .path("/")
                .httpOnly(true)
                .build()))
        .then();
  }

  @PatchMapping("/{id}")
  public Mono<ApplicantResponse> update(@PathVariable UUID id,
      @RequestBody ApplicantInsertRequest request) {
    Applicant applicant = request.exportEntity();
    applicant.setUuid(id);
    return applicantService.update(request.exportEntity()).map(ApplicantResponse::new);
  }

  @PostMapping("/login")
  public Mono<Void> login(ServerWebExchange exchange, @RequestBody ApplicantLoginRequest request) {
    return applicantService.login(request.getEmail(), request.getPassword())
        .map(jwtService::encodeApplicant)
        .map(base64Service::encode)
        .doOnNext(jwt -> exchange.getResponse()
            .addCookie(ResponseCookie.from(CookieKeys.APPLICANT_TOKEN, jwt).path("/").httpOnly(true)
                .build()))
        .then();
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return applicantService.deleteById(id);
  }
}
