package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Applicant;
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
class ApplicantRepositoryTest {

  @Autowired
  ApplicantRepository applicantRepository;

  @Nested
  class findAll {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("全件取得できること")
      void findAll() {
        // when
        Flux<Applicant> actual = applicantRepository.findAll(
            Sort.by(Sort.Direction.DESC, "updatedAt"));
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "三郎",
                    "佐藤", "zzz@example.org", "090-1111-2222",
                    "東京都千代田区", ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abd"), "次郎",
                    "鈴木", "yyy@example.org", "090-9876-5432", "東京都新宿区",
                    ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getFirstName, Applicant::getLastName, Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abe"), "太郎",
                    "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区",
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
        // when
        Mono<Applicant> actual = applicantRepository.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎",
                    "山田", "xxx@example.org"));
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
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎",
                    "山田", "xxx@example.org"));
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
        Applicant applicant = Applicant.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abe")).firstName("四郎")
            .lastName("田中")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("").build();
        // when
        Mono<Applicant> actual = applicantRepository.save(applicant);
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail,
                    Applicant::getPhone, Applicant::getAddress, Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abe"), "四郎",
                    "田中", "aaa@example.org", "090-3333-4444", "東京都港区",
                    ""));
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
        // when
        Mono<Void> actual = applicantRepository.deleteByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}