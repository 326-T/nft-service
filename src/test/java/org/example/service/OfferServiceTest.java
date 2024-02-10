package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.persistence.entity.Offer;
import org.example.persistence.repository.OfferRepository;
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
class OfferServiceTest {

  @InjectMocks
  private OfferService offerService;
  @Mock
  private OfferRepository offerRepository;
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
        Offer offer1 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        Offer offer2 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
                .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
                .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
                .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        Offer offer3 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abe"))
                .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abe"))
                .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abe"))
                .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerRepository.findAll(any(Sort.class)))
            .thenReturn(Flux.just(offer3, offer2, offer1));
        // when
        Flux<Offer> actual = offerService.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                    0.01F, "よろしくお願いします。", 0))
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                    0.01F, "よろしくお願いします。", 0))
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01F, "よろしくお願いします。", 0))
            .verifyComplete();
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
        Offer offer1 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerRepository.findByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .thenReturn(Mono.just(offer1));
        // when
        Mono<Offer> actual = offerService.findByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01F, "よろしくお願いします。", 0))
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
        Offer offer1 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerRepository.save(offer1)).thenReturn(Mono.just(offer1));
        // when
        Mono<Offer> actual = offerService.save(offer1);
        // then
        StepVerifier.create(actual)
            .assertNext(offer -> assertThat(offer)
                .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                    Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    0.01F, "よろしくお願いします。", 0))
            .verifyComplete();
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
        when(offerRepository.deleteByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when
        Mono<Void> actual = offerService.deleteById(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}