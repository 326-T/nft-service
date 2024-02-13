package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.example.persistence.dto.OfferDetailView;
import org.example.persistence.entity.Offer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataR2dbcTest
class OfferDetailViewRepositoryTest {

  @Autowired
  OfferDetailViewRepository offerDetailViewRepository;

  @Nested
  class FindByResumeUuid {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("指定したUUIDに一致するOfferを取得できること")
      void findByResumeUuid() {
        // when
        Flux<OfferDetailView> actual = offerDetailViewRepository.findByResumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(OfferDetailView::getUuid, OfferDetailView::getResumeUuid,
                    OfferDetailView::getCompanyUuid, OfferDetailView::getCompanyName,
                    OfferDetailView::getPrice, OfferDetailView::getMessage, OfferDetailView::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    "株式会社A", 0.01, "よろしくお願いします。", 0))
            .expectComplete();
      }
    }
  }
}