package org.example.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.example.error.exception.NotFoundException;
import org.example.error.exception.PasswordAuthenticationException;
import org.example.persistence.entity.Company;
import org.example.persistence.repository.CompanyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final PasswordEncoder passwordEncoder;

  public CompanyService(CompanyRepository companyRepository,
      PasswordEncoder passwordEncoder) {
    this.companyRepository = companyRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Flux<Company> findAll() {
    return companyRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
  }

  public Mono<Company> findByUuid(UUID id) {
    return companyRepository.findByUuid(id);
  }

  public Mono<Company> findByEmail(String email) {
    return companyRepository.findByEmail(email);
  }

  public Mono<Company> save(Company company, String password) {
    company.setPasswordDigest(passwordEncoder.encode(password));
    return companyRepository.save(company);
  }

  public Mono<Company> update(Company company) {
    return companyRepository.findByUuid(company.getUuid())
        .switchIfEmpty(Mono.error(new NotFoundException("Company not found.")))
        .map(old -> Company.builder()
            .id(old.getId())
            .uuid(old.getUuid())
            .name(company.getName())
            .email(company.getEmail())
            .phone(company.getPhone())
            .address(company.getAddress())
            .createdAt(old.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .version(old.getVersion() + 1)
            .passwordDigest(old.getPasswordDigest())
            .build())
        .flatMap(companyRepository::save);
  }

  public Mono<Company> login(String email, String password) {
    return companyRepository.findByEmail(email)
        .filter(present -> passwordEncoder.matches(password, present.getPasswordDigest()))
        .switchIfEmpty(
            Mono.error(new PasswordAuthenticationException("Invalid email or password.")));
  }

  public Mono<Void> deleteById(UUID id) {
    return companyRepository.deleteByUuid(id);
  }
}
