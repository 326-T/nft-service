package org.example.web.controller;

import java.util.UUID;
import org.example.persistence.entity.Applicant;
import org.example.service.ApplicantService;
import org.example.service.JwtService;
import org.example.web.request.ApplicantLoginRequest;
import org.example.web.request.ApplicantInsertRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  public ApplicantController(ApplicantService applicantService, JwtService jwtService) {
    this.applicantService = applicantService;
    this.jwtService = jwtService;
  }

  @GetMapping
  public Flux<Applicant> index() {
    return applicantService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Applicant> findByUuid(@PathVariable UUID id) {
    return applicantService.findByUuid(id);
  }

  @PostMapping
  public Mono<Applicant> save(ServerWebExchange exchange, @RequestBody ApplicantInsertRequest request) {
    return applicantService.save(request.exportEntity(), request.getPassword())
        .doOnNext(applicant -> exchange.getResponse().addCookie(
            ResponseCookie
                .from("token", jwtService.encodeApplicant(applicant))
                .path("/")
                .httpOnly(true)
                .build()));
  }

  @PostMapping("/login")
  public Mono<Void> login(ServerWebExchange exchange, @RequestBody ApplicantLoginRequest request) {
    return applicantService.login(request.getEmail(), request.getPassword())
        .map(jwtService::encodeApplicant)
        .doOnNext(jwt -> exchange.getResponse()
            .addCookie(ResponseCookie.from("token", jwt).path("/").httpOnly(true).build()))
        .then();
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return applicantService.deleteById(id);
  }
}
