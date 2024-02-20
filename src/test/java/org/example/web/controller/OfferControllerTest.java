package org.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.constant.ContextKeys;
import org.example.persistence.dto.OfferDetailView;
import org.example.persistence.entity.Company;
import org.example.persistence.entity.Offer;
import org.example.service.OfferService;
import org.example.service.ReactiveContextService;
import org.example.web.filter.AuthenticationWebFilter;
import org.example.web.filter.AuthorizationWebFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(
    controllers = OfferController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {AuthenticationWebFilter.class, AuthorizationWebFilter.class})})
@AutoConfigureWebTestClient
class OfferControllerTest {

  @MockBean
  private OfferService offerService;
  @MockBean
  private ReactiveContextService reactiveContextService;
  @Autowired
  private WebTestClient webTestClient;

  @Nested
  class Index {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを全件取得できる")
      void findAllTheOffers() {
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
        when(offerService.findAll())
            .thenReturn(Flux.just(offer3, offer2, offer1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Offer.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(
                        tuple(UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            0.01F, "よろしくお願いします。", 0),
                        tuple(UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            0.01F, "よろしくお願いします。", 0),
                        tuple(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            0.01F, "よろしくお願いします。", 0)
                    )
            );
      }
    }
  }

  @Nested
  class FindById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを1件取得できる")
      void canFindTheOffer() {
        // given
        Offer offer1 = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerService.findByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .thenReturn(Mono.just(offer1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers/12345678-1234-1234-1234-123456789abc")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Offer.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        0.01F, "よろしくお願いします。", 0)
            );
      }
    }
  }

  @Nested
  class FindByResumeUuid {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("ResumeUUIDに一致するオファーを全件取得できる")
      void canFindAllTheOffersByResumeUuid() {
        // given
        OfferDetailView offer1 = OfferDetailView.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .companyName("株式会社A")
            .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerService.findByResumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .thenReturn(Flux.just(offer1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers/resume/12345678-1234-1234-1234-123456789abc")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OfferDetailView.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(OfferDetailView::getUuid, OfferDetailView::getResumeUuid,
                        OfferDetailView::getCompanyUuid, OfferDetailView::getCompanyName,
                        OfferDetailView::getPrice, OfferDetailView::getMessage, OfferDetailView::getStatusId)
                    .containsExactly(
                        tuple(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        "株式会社A", 0.01F, "よろしくお願いします。", 0))
            );
      }
    }
  }

  @Nested
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを登録できる")
      void canSaveTheOffer() {
        // given
        Offer offer = Offer.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .resumeUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .companyUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .price(0.01F).message("よろしくお願いします。").statusId(0).build();
        when(offerService.save(any(Offer.class)))
            .thenReturn(Mono.just(offer));
        when(reactiveContextService.getAttribute(any(ServerWebExchange.class),
            any(ContextKeys.class)))
            .thenReturn(
                Company.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                    .build());
        // when, then
        webTestClient.post()
            .uri("/api/v1/offers")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "resumeUuid": "12345678-1234-1234-1234-123456789abc",
                  "price": 0.01,
                  "message": "よろしくお願いします。"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Offer.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        0.01F, "よろしくお願いします。", 0)
            );
      }
    }
  }

  @Nested
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを1件削除できる")
      void canDeleteTheOffer() {
        // given
        when(offerService.deleteById(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when, then
        webTestClient.delete()
            .uri("/api/v1/offers/12345678-1234-1234-1234-123456789abc")
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }
  }
}