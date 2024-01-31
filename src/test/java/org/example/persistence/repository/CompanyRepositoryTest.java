package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.config.MongoAuditingConfiguration;
import org.example.persistence.entity.Company;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(MongoAuditingConfiguration.class)
@DirtiesContext
class CompanyRepositoryTest {

  @Autowired
  CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    companyRepository.save(
        Company.builder().name("A株式会社").email("xxx@example.org")
            .phone("090-1234-5678").address("東京都渋谷区").passwordDigest("").build()).block();
    companyRepository.save(
        Company.builder().name("B株式会社").email("yyy@example.org")
            .phone("090-9876-5432").address("東京都新宿区").passwordDigest("").build()).block();
    companyRepository.save(
        Company.builder().name("C株式会社").email("zzz@example.org")
            .phone("090-1111-2222").address("東京都千代田区").passwordDigest("").build()).block();
  }

  @AfterEach
  void tearDown() {
    companyRepository.deleteAll().block();
  }

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
                    ""));
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
        // given
        String id = companyRepository.findByEmail("xxx@example.org").block().getId();
        // when
        Mono<Company> actual = companyRepository.findById(id);
        // then
        StepVerifier.create(actual)
            .assertNext(company -> assertThat(company)
                .extracting(Company::getName, Company::getEmail,
                    Company::getPhone, Company::getAddress, Company::getPasswordDigest)
                .containsExactly("A株式会社", "xxx@example.org"));
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
                .containsExactly("A株式会社", "xxx@example.org"));
      }
    }
  }

  @Nested
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
                    ""));
      }
    }
  }

  @Nested
  class deleteById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで削除できること")
      void deleteById() {
        // given
        String id = companyRepository.findByEmail("xxx@example.org").block().getId();
        // when
        Mono<Void> actual = companyRepository.deleteById(id);
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}