package org.example.it;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.UUID;
import org.example.Main;
import org.example.error.response.ErrorResponse;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Applicant;
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
public class ApplicantAPITest {

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private Base64Service base64Service;
  private String jwt;


  @BeforeEach
  void setUp() {
    jwt = base64Service.encode(jwtService.encodeApplicant(
        Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).build()));
  }

  @Nested
  class Index {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("応募者を全件取得できる")
      void findAllTheApplicants() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/applicants")
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Applicant.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getId, Applicant::getUuid, Applicant::getFirstName,
                        Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly(
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abe"), "三郎",
                            "佐藤", "zzz@example.org", "090-1111-2222", "東京都千代田区", null),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abd"), "次郎",
                            "鈴木", "yyy@example.org", "090-9876-5432", "東京都新宿区", null),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎",
                            "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区", null)
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
            .uri("/api/v1/applicants")
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
                        "org.example.error.exception.UnauthenticatedException: Authorization headerがありません。",
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
            .uri("/api/v1/applicants/%s".formatted(
                UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Applicant.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getId, Applicant::getUuid, Applicant::getFirstName,
                        Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        "太郎", "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区", null
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
            .uri("/api/v1/applicants/%s".formatted(
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
                        "org.example.error.exception.UnauthenticatedException: Authorization headerがありません。",
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
      @DisplayName("応募者を1件登録できる")
      void canSaveTheApplicant() {
        // when, then
        webTestClient.post()
            .uri("/api/v1/applicants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "firstName": "四郎",
                  "lastName": "田中",
                  "email": "aaa@example.org",
                  "phone": "090-3333-4444",
                  "address": "東京都港区",
                  "password": "password"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectCookie().value("applicant-token", jwt -> {
              Applicant applicant = jwtService.decodeApplicant(base64Service.decode(jwt));
              assertThat(applicant)
                  .extracting(Applicant::getFirstName, Applicant::getLastName,
                      Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                      Applicant::getPasswordDigest)
                  .containsExactly("四郎", "田中", "aaa@example.org", "090-3333-4444",
                      "東京都港区", null);
            });
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
        // when, then
        webTestClient.post()
            .uri("/api/v1/applicants/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "email": "xxx@example.org",
                  "password": "password"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectCookie().value("applicant-token", jwt -> {
              Applicant applicant = jwtService.decodeApplicant(base64Service.decode(jwt));
              assertThat(applicant)
                  .extracting(Applicant::getFirstName, Applicant::getLastName,
                      Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                      Applicant::getPasswordDigest)
                  .containsExactly("太郎", "山田", "xxx@example.org", "090-1234-5678",
                      "東京都渋谷区", null
                  );
            });
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("emailが間違っている")
      void emailError() {
        // when, then
        // when, then
        webTestClient.post()
            .uri("/api/v1/applicants/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "email": "invalid@example.org",
                  "password": "password"
                }
                """)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "emailまたはpasswordが間違っている",
                        "org.example.error.exception.PasswordAuthenticationException: Invalid email or password.",
                        "emailまたはpasswordが間違っています。")
            );
      }

      @Test
      @DisplayName("passwordが間違っている")
      void passwordError() {
        // when, then
        // when, then
        webTestClient.post()
            .uri("/api/v1/applicants/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "email": "xxx@example.org",
                  "password": "invalid"
                }
                """)
            .exchange()
            .expectStatus().isUnauthorized()
            .expectBody(ErrorResponse.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(ErrorResponse::getStatus, ErrorResponse::getCode,
                        ErrorResponse::getSummary, ErrorResponse::getDetail,
                        ErrorResponse::getMessage)
                    .containsExactly(401, null,
                        "emailまたはpasswordが間違っている",
                        "org.example.error.exception.PasswordAuthenticationException: Invalid email or password.",
                        "emailまたはpasswordが間違っています。")
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
      @DisplayName("応募者を1件削除できる")
      void canDeleteTheApplicant() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/applicants/12345678-1234-1234-1234-123456789abc")
            .cookie("token", jwt)
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
            .uri("/api/v1/applicants/%s".formatted(
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
                        "org.example.error.exception.UnauthenticatedException: Authorization headerがありません。",
                        "JWTが有効ではありません。")
            );
      }
    }
  }
}
