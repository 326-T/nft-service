package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Company;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class CompanyRepositoryTest {

  @Autowired
  CompanyRepository companyRepository;

  @Nested
  class findAll {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("全件取得できること")
      void findAll() {
        // when
        Flux<Company> actual = companyRepository.findAll(
            Sort.by(Sort.Direction.DESC, "updatedAt"));
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("C株式会社", "zzz@example.org", "090-1111-2222",
                    "東京都千代田区", ""))
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("B株式会社", "yyy@example.org", "090-9876-5432", "東京都新宿区",
                    ""))
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("A株式会社", "xxx@example.org", "090-1234-5678", "東京都渋谷区",
                    ""))
            .expectComplete();
      }
    }
  }

  @Nested
  class findById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで検索できること")
      void findById() {
        // when
        Mono<Company> actual = companyRepository.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("A株式会社", "xxx@example.org"))
            .expectComplete();
      }
    }
  }

  @Nested
  class findByEmail {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("Emailで検索できること")
      void findByEmail() {
        // when
        Mono<Company> actual = companyRepository.findByEmail("xxx@example.org");
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("A株式会社", "xxx@example.org"))
            .expectComplete();
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class save {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("保存できること")
      void save() {
        // given
        Company company = Company.builder().name("D株式会社")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("").build();
        // when
        Mono<Company> actual = companyRepository.save(company);
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("D株式会社", "aaa@example.org", "090-3333-4444", "東京都港区",
                    ""))
            .expectComplete();
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class deleteById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで削除できること")
      void deleteById() {
        // given
        UUID id = companyRepository.findByEmail("xxx@example.org").block().getUuid();
        // when
        Mono<Void> actual = companyRepository.deleteByUuid(id);
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}