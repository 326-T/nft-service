package org.example.it;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.UUID;
import org.example.Main;
import org.example.error.response.ErrorResponse;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Resume;
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
public class ResumeAPITest {

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
      void findAllTheResumes() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes")
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Resume.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getUuid, Resume::getApplicantUuid,
                        Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getUrls)
                    .containsExactly(
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                            "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級", "外資企業",
                            "https://imageA.png"),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            "2020年 B大学卒業", "コンビニバイト", "TOEIC 900点", "ベンチャー企業",
                            "https://imageB.png"),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                            "https://imageC.png")
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
            .uri("/api/v1/resumes")
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
            .uri("/api/v1/resumes/12345678-1234-1234-1234-123456789abc")
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getUuid, Resume::getApplicantUuid,
                        Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getUrls)
                    .containsExactly(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                        "https://imageC.png")
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
            .uri("/api/v1/resumes/%s".formatted(
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
  class FindByApplicantId {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("applicantIdで検索できる")
      void canFindByApplicantId() {
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes/applicant")
            .cookie("token", jwt)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getUuid, Resume::getApplicantUuid,
                        Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getUrls)
                    .containsExactly(
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                            "https://imageC.png"))
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
            .uri("/api/v1/resumes/applicant/1")
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
      void canSaveTheResume() {
        // when, then
        webTestClient.post()
            .uri("/api/v1/resumes")
            .cookie("token", jwt)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "applicantUuid": "12345678-1234-1234-1234-123456789abc",
                  "education": "2021年 A大学卒業",
                  "experience": "居酒屋バイトリーダー",
                  "skills": "英検1級",
                  "interests": "外資企業",
                  "urls": "https://imageA.png"
                }
                """
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getApplicantUuid, Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getUrls)
                    .containsExactly(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                        "外資企業", "https://imageA.png")
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
            .uri("/api/v1/resumes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "applicantUuid": "12345678-1234-1234-1234-123456789abc",
                  "education": "2021年 A大学卒業",
                  "experience": "居酒屋バイトリーダー",
                  "skills": "英検1級",
                  "interests": "外資企業",
                  "urls": "https://imageA.png"
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
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("応募者を1件削除できる")
      void canDeleteTheResume() {
        // when, then
        webTestClient.delete()
            .uri("/api/v1/resumes/12345678-1234-1234-1234-123456789abc")
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
            .uri("/api/v1/resumes/%s".formatted(
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
