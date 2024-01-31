package org.example.service;

import java.util.UUID;
import org.example.persistence.entity.Resume;
import org.example.persistence.repository.ResumeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;

  public ResumeService(ResumeRepository resumeRepository) {
    this.resumeRepository = resumeRepository;
  }

  public Flux<Resume> findAll() {
    return resumeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Resume> findByUuid(UUID id) {
    return resumeRepository.findByUuid(id);
  }

  public Flux<Resume> findByApplicantId(UUID resumeId) {
    return resumeRepository.findByApplicantId(resumeId);
  }

  public Mono<Resume> insert(Resume resume) {
    return resumeRepository.save(resume);
  }

  public Mono<Void> deleteById(UUID id) {
    return resumeRepository.deleteById(id);
  }
}
