package org.example.it;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import org.example.Main;
import org.example.error.response.ErrorResponse;
import org.example.persistence.entity.Applicant;
import org.example.persistence.repository.ApplicantRepository;
import org.example.service.JwtService;
import org.junit.jupiter.api.AfterEach;
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
  ApplicantRepository applicantRepository;
  private String id;
  private String jwt;


  @BeforeEach
  void setUp() {
    applicantRepository.save(
            Applicant.builder().firstName("太郎").lastName("山田").email("xxx@example.org")
                .phone("090-1234-5678").address("東京都渋谷区")
                .passwordDigest("$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu").build())
        .block();
    applicantRepository.save(
            Applicant.builder().firstName("次郎").lastName("鈴木").email("yyy@example.org")
                .phone("090-9876-5432").address("東京都新宿区")
                .passwordDigest("$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu").build())
        .block();
    applicantRepository.save(
            Applicant.builder().firstName("三郎").lastName("佐藤").email("zzz@example.org")
                .phone("090-1111-2222").address("東京都千代田区")
                .passwordDigest("$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu").build())
        .block();
    id = applicantRepository.findByEmail("xxx@example.org").block().getId();
    jwt = jwtService.encode(Applicant.builder().id("1").build());
  }

  @AfterEach
  void tearDown() {
    applicantRepository.deleteAll().block();
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
                    .extracting(Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly(
                        tuple("三郎", "佐藤", "zzz@example.org", "090-1111-2222",
                            "東京都千代田区",
                            "$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu"),
                        tuple("次郎", "鈴木", "yyy@example.org", "090-9876-5432",
                            "東京都新宿区",
                            "$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu"),
                        tuple("太郎", "山田", "xxx@example.org", "090-1234-5678",
                            "東京都渋谷区",
                            "$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu")
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
            .uri("/api/v1/applicants/%s".formatted(id))
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Applicant.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly("太郎", "山田", "xxx@example.org", "090-1234-5678",
                        "東京都渋谷区",
                        "$2a$10$d3K9jDtqZ3Hi44S6ByqUxuZszfQuCNHSob2Cl/k2ZoIReIcTSldUu"
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
            .uri("/api/v1/applicants/%s".formatted(id))
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
            .expectBody(Applicant.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress)
                    .containsExactly("四郎", "田中", "aaa@example.org", "090-3333-4444",
                        "東京都港区")
            );
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
            .expectCookie().value("token", jwt -> {
              Applicant applicant = jwtService.decode(jwt);
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
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("応募者を1件削除できる")
      void canDeleteTheApplicant() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/applicants/1")
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
            .uri("/api/v1/applicants/%s".formatted(id))
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
