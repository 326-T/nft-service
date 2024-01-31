package org.example.web.controller;

import java.util.UUID;
import org.example.persistence.entity.Resume;
import org.example.service.ResumeService;
import org.example.web.request.ResumeInsertRequest;
import org.example.web.response.ResumeResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

  private final ResumeService resumeService;

  public ResumeController(ResumeService resumeService) {
    this.resumeService = resumeService;
  }

  @GetMapping
  public Flux<ResumeResponse> index() {
    return resumeService.findAll().map(ResumeResponse::new);
  }

  @GetMapping("/{id}")
  public Mono<ResumeResponse> findByUuid(@PathVariable UUID id) {
    return resumeService.findByUuid(id).map(ResumeResponse::new);
  }

  @GetMapping("/applicant/{applicantId}")
  public Flux<ResumeResponse> findByApplicantId(@PathVariable UUID applicantId) {
    return resumeService.findByApplicantId(applicantId).map(ResumeResponse::new);
  }

  @PostMapping
  public Mono<ResumeResponse> save(@RequestBody ResumeInsertRequest request) {
    return resumeService.insert(request.exportEntity()).map(ResumeResponse::new);
  }

  @PatchMapping("/{id}")
  public Mono<ResumeResponse> update(@PathVariable UUID id, @RequestBody ResumeInsertRequest request) {
    Resume resume = request.exportEntity();
    resume.setUuid(id);
    return resumeService.update(request.exportEntity()).map(ResumeResponse::new);
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return resumeService.deleteById(id);
  }
}
