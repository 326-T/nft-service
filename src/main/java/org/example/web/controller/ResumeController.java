package org.example.web.controller;

import org.example.persistence.entity.Resume;
import org.example.service.ResumeService;
import org.example.web.request.ResumeRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  public Mono<Resume> findById(@PathVariable String id) {
    return resumeService.findById(id);
  }

  @GetMapping("/applicant/{applicantId}")
  public Flux<Resume> findByApplicantId(@PathVariable String applicantId) {
    return resumeService.findByApplicantId(applicantId);
  }

  @PostMapping
  public Mono<Resume> save(@RequestBody ResumeRequest request) {
    return resumeService.save(request.exportEntity());
  }

  @DeleteMapping("/{id}")
  public Mono<Void> deleteById(@PathVariable String id) {
    return resumeService.deleteById(id);
  }
}
