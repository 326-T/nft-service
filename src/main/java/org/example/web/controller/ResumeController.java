package org.example.web.controller;

import java.util.UUID;
import org.example.persistence.entity.Resume;
import org.example.service.ResumeService;
import org.example.web.request.ResumeInsertRequest;
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
  public Flux<Resume> index() {
    return resumeService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Resume> findByUuid(@PathVariable UUID id) {
    return resumeService.findByUuid(id);
  }

  @GetMapping("/applicant/{applicantId}")
  public Flux<Resume> findByApplicantId(@PathVariable UUID applicantId) {
    return resumeService.findByApplicantId(applicantId);
  }

  @PostMapping
  public Mono<Resume> save(@RequestBody ResumeInsertRequest request) {
    return resumeService.insert(request.exportEntity());
  }

  @PatchMapping("/{id}")
  public Mono<Resume> update(@PathVariable UUID id, @RequestBody ResumeInsertRequest request) {
    Resume resume = request.exportEntity();
    resume.setUuid(id);
    return resumeService.update(request.exportEntity());
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return resumeService.deleteById(id);
  }
}
