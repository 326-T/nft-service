package org.example.service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.example.constant.MintStatus;
import org.example.constant.OfferStatus;
import org.example.error.exception.NotFoundException;
import org.example.persistence.entity.Resume;
import org.example.persistence.repository.OfferRepository;
import org.example.persistence.repository.ResumeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final OfferRepository offerRepository;

  public ResumeService(ResumeRepository resumeRepository, OfferRepository offerRepository) {
    this.resumeRepository = resumeRepository;
    this.offerRepository = offerRepository;
  }

  public Flux<Resume> findAll() {
    return resumeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Resume> findByUuid(UUID id) {
    return resumeRepository.findByUuid(id);
  }

  public Flux<Resume> findByMintStatusId(Integer mintStatusId) {
    return resumeRepository.findByMintStatusId(mintStatusId);
  }

  public Flux<Resume> findByApplicantId(UUID resumeId) {
    return resumeRepository.findByApplicantUuid(resumeId);
  }

  public Mono<Resume> insert(Resume resume) {
    return resumeRepository.save(resume);
  }

  public Mono<Resume> update(Resume resume) {
    return resumeRepository.findByUuid(resume.getUuid())
        .switchIfEmpty(Mono.error(new NotFoundException("Resume not found.")))
        .map(old -> Resume.builder()
            .id(old.getId())
            .uuid(old.getUuid())
            .applicantUuid(old.getApplicantUuid())
            .education(resume.getEducation())
            .experience(resume.getExperience())
            .skills(resume.getSkills())
            .interests(resume.getInterests())
            .urls(resume.getUrls())
            .createdAt(old.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .version(old.getVersion())
            .build())
        .flatMap(resumeRepository::save);
  }

  public Mono<Resume> mint(UUID uuid, Float price) {
    return resumeRepository.findByUuid(uuid)
        .switchIfEmpty(Mono.error(new NotFoundException("Resume not found.")))
        .map(old -> {
          old.setMinimumPrice(price);
          old.setMintStatusId(MintStatus.PUBLISHED.getId());
          old.setUpdatedAt(LocalDateTime.now());
          return old;
        })
        .flatMap(resumeRepository::save);
  }

  public Mono<Resume> expire(UUID uuid) {
    Mono<Resume> resumeMono = resumeRepository.findByUuid(uuid)
        .switchIfEmpty(Mono.error(new NotFoundException("Resume not found.")));
    return resumeMono.flatMapMany(resume -> offerRepository.findByResumeUuid(resume.getUuid()))
        .filter(offer -> Objects.equals(offer.getStatusId(), OfferStatus.PENDING.getId()))
        .map(offer -> {
          offer.setStatusId(OfferStatus.REJECTED.getId());
          return offer;
        })
        .flatMap(offerRepository::save)
        .then(resumeMono)
        .map(old -> {
          old.setMintStatusId(MintStatus.EXPIRED.getId());
          old.setUpdatedAt(LocalDateTime.now());
          return old;
        })
        .flatMap(resumeRepository::save);
  }

  public Mono<Void> deleteById(UUID id) {
    return resumeRepository.deleteByUuid(id);
  }
}
