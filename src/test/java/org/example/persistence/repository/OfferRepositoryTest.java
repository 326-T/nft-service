package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Offer;
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
class OfferRepositoryTest {

  @Autowired
  OfferRepository offerRepository;

  @Nested
  class findAll {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("全件取得できること")
      void findAll() {
        // when
        Flux<Offer> actual = offerRepository.findAll(
            Sort.by(Sort.Direction.DESC, "updatedAt"));
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01, "よろしくお願いします。", 0))
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01, "よろしくお願いします。", 0))
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01, "よろしくお願いします。", 0))
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
        Mono<Offer> actual = offerRepository.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01, "よろしくお願いします。", 0))
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
        // when
        Mono<Void> actual = offerRepository.deleteByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}