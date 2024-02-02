package org.example.it;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.UUID;
import org.example.Main;
import org.example.error.response.ErrorResponse;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Company;
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
public class CompanyAPITest {

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
      void findAllTheCompanies() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/companies")
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Company.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Company::getId, Company::getUuid, Company::getName,
                        Company::getEmail, Company::getPhone, Company::getAddress,
                        Company::getPasswordDigest)
                    .containsExactly(
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            "C株式会社", "zzz@example.org", "090-1111-2222", "東京都千代田区",
                            null),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            "B株式会社", "yyy@example.org", "090-9876-5432", "東京都新宿区", null),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            "A株式会社", "xxx@example.org", "090-1234-5678", "東京都渋谷区", null)
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
            .uri("/api/v1/companies")
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
            .uri("/api/v1/companies/%s".formatted(
                UUID.fromString("12345678-1234-1234-1234-123456789abc")))
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Company.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Company::getId, Company::getUuid, Company::getName,
                        Company::getEmail, Company::getPhone, Company::getAddress,
                        Company::getPasswordDigest)
                    .containsExactly(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        "A株式会社", "xxx@example.org", "090-1234-5678", "東京都渋谷区", null
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
            .uri("/api/v1/companies/%s".formatted(
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
      void canSaveTheCompany() {
        // when, then
        webTestClient.post()
            .uri("/api/v1/companies")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "name": "D株式会社",
                  "email": "aaa@example.org",
                  "phone": "090-3333-4444",
                  "address": "東京都港区",
                  "password": "password"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectCookie().value("company-token", jwt -> {
              Company company = jwtService.decodeCompany(base64Service.decode(jwt));
              assertThat(company)
                  .extracting(Company::getName,
                      Company::getEmail, Company::getPhone, Company::getAddress,
                      Company::getPasswordDigest)
                  .containsExactly("D株式会社", "aaa@example.org", "090-3333-4444",
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
            .uri("/api/v1/companies/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "email": "xxx@example.org",
                  "password": "password"
                }
                """)
            .exchange()
            .expectStatus().isOk()
            .expectCookie().value("company-token", jwt -> {
              Company company = jwtService.decodeCompany(base64Service.decode(jwt));
              assertThat(company)
                  .extracting(Company::getName,
                      Company::getEmail, Company::getPhone, Company::getAddress,
                      Company::getPasswordDigest)
                  .containsExactly("A株式会社", "xxx@example.org", "090-1234-5678",
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
            .uri("/api/v1/companies/login")
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
            .uri("/api/v1/companies/login")
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
      void canDeleteTheCompany() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/companies/12345678-1234-1234-1234-123456789abc")
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
            .uri("/api/v1/companies/%s".formatted(
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
