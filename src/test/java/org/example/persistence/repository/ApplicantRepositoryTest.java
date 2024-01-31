package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.config.MongoAuditingConfiguration;
import org.example.persistence.entity.Applicant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(MongoAuditingConfiguration.class)
@DirtiesContext
class ApplicantRepositoryTest {

  @Autowired
  ApplicantRepository applicantRepository;

  @BeforeEach
  void setUp() {
    applicantRepository.save(
        Applicant.builder().firstName("太郎").lastName("山田").email("xxx@example.org")
            .phone("090-1234-5678").address("東京都渋谷区").passwordDigest("").build()).block();
    applicantRepository.save(
        Applicant.builder().firstName("次郎").lastName("鈴木").email("yyy@example.org")
            .phone("090-9876-5432").address("東京都新宿区").passwordDigest("").build()).block();
    applicantRepository.save(
        Applicant.builder().firstName("三郎").lastName("佐藤").email("zzz@example.org")
            .phone("090-1111-2222").address("東京都千代田区").passwordDigest("").build()).block();
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
        Flux<Applicant> actual = applicantRepository.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("太郎", "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区",
                    ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("次郎", "鈴木", "yyy@example.org", "090-9876-5432", "東京都新宿区",
                    ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("三郎", "佐藤", "zzz@example.org", "090-1111-2222",
                    "東京都千代田区", ""));
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
        String id = applicantRepository.findByEmail("xxx@example.org").block().getId();
        // when
        Mono<Applicant> actual = applicantRepository.findById(id);
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("太郎", "山田", "xxx@example.org"));
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
        Mono<Applicant> actual = applicantRepository.findByEmail("xxx@example.org");
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("太郎", "山田", "xxx@example.org"));
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
        Applicant applicant = Applicant.builder().firstName("四郎").lastName("田中")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("").build();
        // when
        Mono<Applicant> actual = applicantRepository.save(applicant);
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly("四郎", "田中", "aaa@example.org", "090-3333-4444", "東京都港区",
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
        String id = applicantRepository.findByEmail("xxx@example.org").block().getId();
        // when
        Mono<Void> actual = applicantRepository.deleteById(id);
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}