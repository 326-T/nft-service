package org.example.web.controller;

import java.util.UUID;
import org.example.persistence.entity.Company;
import org.example.service.CompanyService;
import org.example.service.JwtService;
import org.example.web.request.CompanyLoginRequest;
import org.example.web.request.CompanyInsertRequest;
import org.example.web.response.CompanyResponse;
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
@RequestMapping("/api/v1/companies")
public class CompanyController {

  private final CompanyService companyService;
  private final JwtService jwtService;

  public CompanyController(CompanyService companyService, JwtService jwtService) {
    this.companyService = companyService;
    this.jwtService = jwtService;
  }

  @GetMapping
  public Flux<CompanyResponse> index() {
    return companyService.findAll().map(CompanyResponse::new);
  }

  @GetMapping("/{id}")
  public Mono<CompanyResponse> findByUuid(@PathVariable UUID id) {
    return companyService.findByUuid(id).map(CompanyResponse::new);
  }

  @PostMapping
  public Mono<Void> save(ServerWebExchange exchange, @RequestBody CompanyInsertRequest request) {
    return companyService.save(request.exportEntity(), request.getPassword())
        .doOnNext(company -> exchange.getResponse().addCookie(
            ResponseCookie
                .from("token", jwtService.encodeCompany(company))
                .path("/")
                .httpOnly(true)
                .build()))
        .then();
  }

  @PatchMapping("/{id}")
  public Mono<CompanyResponse> update(@PathVariable UUID id, @RequestBody CompanyInsertRequest request) {
    Company company = request.exportEntity();
    company.setUuid(id);
    return companyService.update(request.exportEntity()).map(CompanyResponse::new);
  }

  @PostMapping("/login")
  public Mono<Void> login(ServerWebExchange exchange, @RequestBody CompanyLoginRequest request) {
    return companyService.login(request.getEmail(), request.getPassword())
        .map(jwtService::encodeCompany)
        .doOnNext(jwt -> exchange.getResponse()
            .addCookie(ResponseCookie.from("token", jwt).path("/").httpOnly(true).build()))
        .then();
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return companyService.deleteById(id);
  }
}
