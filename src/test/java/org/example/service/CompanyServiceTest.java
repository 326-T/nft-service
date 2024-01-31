package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.example.error.exception.PasswordAuthenticationException;
import org.example.persistence.entity.Company;
import org.example.persistence.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class CompanyServiceTest {

  @InjectMocks
  private CompanyService companyService;
  @Mock
  private CompanyRepository companyRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Nested
  class FindAll {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("全件取得できる")
      void canFindAll() {
        // given
        Company company1 = Company.builder().id("1").name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        Company company2 = Company.builder().id("2").name("B株式会社")
            .email("yyy@example.org").phone("090-9876-5432").address("東京都新宿区")
            .passwordDigest("").build();
        Company company3 = Company.builder().id("3").name("C株式会社")
            .email("zzz@example.org").phone("090-1111-2222").address("東京都千代田区")
            .passwordDigest("").build();
        when(companyRepository.findAll(any(Sort.class)))
            .thenReturn(Flux.just(company3, company2, company1));
        // when
        Flux<Company> actual = companyService.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getId, Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("1", "A株式会社", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .assertNext(company -> assertThat(company)
                .extracting(Company::getId, Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("2", "B株式会社", "yyy@example.org", "090-9876-5432",
                    "東京都新宿区",
                    ""))
            .assertNext(company -> assertThat(company)
                .extracting(Company::getId, Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("3", "C株式会社", "zzz@example.org", "090-1111-2222",
                    "東京都千代田区", ""));
      }
    }
  }

  @Nested
  class FindById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("IDで検索できる")
      void canFindById() {
        // given
        Company company1 = Company.builder().id("1").name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(companyRepository.findById("1")).thenReturn(Mono.just(company1));
        // when
        Mono<Company> actual = companyService.findById("1");
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getId, Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("1", "A株式会社", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindByEmail {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("emailで検索できる")
      void canFindByEmail() {
        // given
        Company company1 = Company.builder().id("1").name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(companyRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(company1));
        // when
        Mono<Company> actual = companyService.findByEmail("xxx@example.org");
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getId, Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("1", "A株式会社", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .verifyComplete();
      }
    }
  }

  @Nested
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("保存できる")
      void canSave() {
        // given
        Company company1 = Company.builder().name("D株式会社")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("password").build();
        when(companyRepository.save(any(Company.class))).thenReturn(Mono.just(company1));
        when(passwordEncoder.encode("password")).thenReturn("password_digest");
        // when
        Mono<Company> actual = companyService.save(company1, "password");
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("D株式会社", "aaa@example.org", "090-3333-4444", "東京都港区",
                    "password_digest"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class Login {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("ログインできる")
      void canLogin() {
        // given
        Company company1 = Company.builder().id("1").name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("password_digest").build();
        when(companyRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(company1));
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(true);
        // when
        Mono<Company> actual = companyService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Company::getName,
                    Company::getEmail, Company::getPhone, Company::getAddress,
                    Company::getPasswordDigest)
                .containsExactly("A株式会社", "xxx@example.org", "090-1234-5678", "東京都渋谷区",
                    "password_digest"))
            .verifyComplete();
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("emailが間違っている")
      void emailIsWrong() {
        // given
        when(companyRepository.findByEmail("xxx@example.org")).thenReturn(Mono.empty());
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(true);
        // when
        Mono<Company> actual = companyService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual).expectError(PasswordAuthenticationException.class).verify();
      }

      @Test
      @DisplayName("passwordが間違っている")
      void passwordIsWrong() {
        // given
        Company company1 = Company.builder().id("1").name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("password_digest").build();
        when(companyRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(company1));
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(false);
        // when
        Mono<Company> actual = companyService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual).expectError(PasswordAuthenticationException.class).verify();
      }
    }
  }

  @Nested
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("IDで削除できる")
      void canDeleteById() {
        // given
        when(companyRepository.deleteById("1")).thenReturn(Mono.empty());
        // when
        Mono<Void> actual = companyService.deleteById("1");
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}