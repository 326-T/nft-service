package org.example.service;

import lombok.NonNull;
import org.example.persistence.entity.Resume;
import org.example.persistence.repository.ResumeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;

  public ResumeService(ResumeRepository resumeRepository) {
    this.resumeRepository = resumeRepository;
  }

  @Query("{}")
  @NonNull
  public Flux<Resume> findAll() {
    return resumeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Resume> findById(String id) {
    return resumeRepository.findById(id);
  }

  public Flux<Resume> findByApplicantId(String resumeId) {
    return resumeRepository.findByApplicantId(resumeId);
  }

  public Mono<Resume> save(Resume resume) {
    return resumeRepository.save(resume);
  }

  public Mono<Void> deleteById(String id) {
    return resumeRepository.deleteById(id);
  }
}
