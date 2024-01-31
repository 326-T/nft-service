package org.example.service;

import java.util.UUID;
import org.example.error.exception.PasswordAuthenticationException;
import org.example.persistence.entity.Applicant;
import org.example.persistence.repository.ApplicantRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ApplicantService {

  private final ApplicantRepository applicantRepository;
  private final PasswordEncoder passwordEncoder;

  public ApplicantService(ApplicantRepository applicantRepository,
      PasswordEncoder passwordEncoder) {
    this.applicantRepository = applicantRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Flux<Applicant> findAll() {
    return applicantRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Applicant> findByUuid(UUID id) {
    return applicantRepository.findByUuid(id);
  }

  public Mono<Applicant> findByEmail(String email) {
    return applicantRepository.findByEmail(email);
  }

  public Mono<Applicant> save(Applicant applicant, String password) {
    applicant.setPasswordDigest(passwordEncoder.encode(password));
    return applicantRepository.save(applicant);
  }

  public Mono<Applicant> login(String email, String password) {
    return applicantRepository.findByEmail(email)
        .filter(present -> passwordEncoder.matches(password, present.getPasswordDigest()))
        .switchIfEmpty(
            Mono.error(new PasswordAuthenticationException("Invalid email or password.")));
  }

  public Mono<Void> deleteById(UUID id) {
    return applicantRepository.deleteById(id);
  }
}
