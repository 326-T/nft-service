package org.example.it;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.UUID;
import org.example.Main;
import org.example.constant.CookieKeys;
import org.example.error.response.ErrorResponse;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Company;
import org.example.persistence.entity.Offer;
import org.example.service.Base64Service;
import org.example.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureWebClient
public class OfferAPITest {

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private Base64Service base64Service;
  private String jwt;
  private String companyJwt;


  @BeforeEach
  void setUp() {
    jwt = base64Service.encode(jwtService.encodeApplicant(
        Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).build()));
    companyJwt = base64Service.encode(jwtService.encodeCompany(
        Company.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).build()));
  }

  @Nested
  class Index {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを全件取得できる")
      void findAllTheOffers() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers")
            .cookie(CookieKeys.APPLICANT_TOKEN, jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Offer.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(
                        tuple(UUID.fromString("12345678-1234-5678-1234-123456789abe"),
                            UUID.fromString("12345678-1234-5678-1234-123456789abe"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            0.01F, "よろしくお願いします。", 0),
                        tuple(UUID.fromString("12345678-1234-5678-1234-123456789abd"),
                            UUID.fromString("12345678-1234-5678-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            0.01F, "よろしくお願いします。", 0),
                        tuple(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                            UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            0.01F, "よろしくお願いします。", 0)
                    )

            );
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("認証エラー")
      void authenticationError() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers")
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "クライアント側の認証切れ",
                        "org.example.error.exception.ForbiddenException: 認可されていません。",
                        "JWTが有効ではありません。")
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
      @DisplayName("IDで検索できる")
      void canFindById() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers/12345678-1234-5678-1234-123456789abc")
            .cookie(CookieKeys.APPLICANT_TOKEN, jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Offer.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getUuid, Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                        UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        0.01F, "よろしくお願いします。", 0));
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("認証エラー")
      void authenticationError() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/offers/%s".formatted(
                UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "クライアント側の認証切れ",
                        "org.example.error.exception.ForbiddenException: 認可されていません。",
                        "JWTが有効ではありません。")
            );
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを1件登録できる")
      void canSaveTheOffer() {
        // when, then
        webTestClient.post()
            .uri("/api/v1/offers")
            .cookie(CookieKeys.COMPANY_TOKEN, companyJwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "resumeUuid": "12345678-1234-5678-1234-123456789abd",
                  "price": 0.01,
                  "message": "よろしくお願いします。"
                }
                """
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(Offer.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Offer::getResumeUuid, Offer::getCompanyUuid,
                        Offer::getPrice, Offer::getMessage, Offer::getStatusId)
                    .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abd"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        0.01F, "よろしくお願いします。", 0)
            );
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("認証エラー")
      void authenticationError() {
        // when, then
        webTestClient.post()
            .uri("/api/v1/offers")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "resumeUuid": "12345678-1234-1234-1234-123456789abd",
                  "price": 0.01,
                  "message": "よろしくお願いします。"
                }
                """
            )
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "クライアント側の認証切れ",
                        "org.example.error.exception.ForbiddenException: 認可されていません。",
                        "JWTが有効ではありません。")
            );
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("オファーを1件削除できる")
      void canDeleteTheOffer() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/offers/12345678-1234-1234-1234-123456789abc")
            .cookie(CookieKeys.COMPANY_TOKEN, companyJwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("認証エラー")
      void authenticationError() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/offers/%s".formatted(
                UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "クライアント側の認証切れ",
                        "org.example.error.exception.ForbiddenException: 認可されていません。",
                        "JWTが有効ではありません。")
            );
      }
    }
  }
}
