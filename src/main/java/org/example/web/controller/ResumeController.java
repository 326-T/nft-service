package org.example.web.controller;

import java.util.UUID;
import org.example.constant.ContextKeys;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Resume;
import org.example.service.ReactiveContextService;
import org.example.service.ResumeService;
import org.example.web.request.ResumeInsertRequest;
import org.example.web.request.ResumeUpdateRequest;
import org.example.web.response.ResumeResponse;
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
@RequestMapping("/api/v1/resumes")
public class ResumeController {

  private final ResumeService resumeService;
  private final ReactiveContextService reactiveContextService;

  public ResumeController(ResumeService resumeService,
      ReactiveContextService reactiveContextService) {
    this.resumeService = resumeService;
    this.reactiveContextService = reactiveContextService;
  }

  @GetMapping
  public Flux<ResumeResponse> index() {
    return resumeService.findAll().map(ResumeResponse::new);
  }

  @GetMapping("/{id}")
  public Mono<ResumeResponse> findByUuid(@PathVariable UUID id) {
    return resumeService.findByUuid(id).map(ResumeResponse::new);
  }

  @GetMapping("/applicant")
  public Flux<ResumeResponse> findByApplicantId(ServerWebExchange exchange) {
    Applicant applicant = reactiveContextService.getAttribute(exchange, ContextKeys.APPLICANT_KEY);
    UUID uuid = applicant.getUuid();
    return resumeService.findByApplicantId(uuid).map(ResumeResponse::new);
  }

  @PostMapping
  public Mono<ResumeResponse> save(ServerWebExchange exchange,
      @RequestBody ResumeInsertRequest request) {
    Resume resume = request.exportEntity();
    Applicant applicant = reactiveContextService.getAttribute(exchange, ContextKeys.APPLICANT_KEY);
    UUID uuid = applicant.getUuid();
    resume.setApplicantUuid(uuid);
    return resumeService.insert(resume).map(ResumeResponse::new);
  }

  @PatchMapping("/{id}")
  public Mono<ResumeResponse> update(@PathVariable UUID id,
      @RequestBody ResumeUpdateRequest request) {
    Resume resume = request.exportEntity();
    resume.setUuid(id);
    return resumeService.update(request.exportEntity()).map(ResumeResponse::new);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return resumeService.deleteById(id);
  }
}
