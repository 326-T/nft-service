package org.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.persistence.entity.Applicant;
import org.example.persistence.entity.Resume;
import org.example.service.JwtService;
import org.example.service.ReactiveContextService;
import org.example.service.ResumeService;
import org.example.web.filter.AuthenticationWebFilter;
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
    controllers = ResumeController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {AuthenticationWebFilter.class})})
@AutoConfigureWebTestClient
class ResumeControllerTest {

  @MockBean
  private ResumeService resumeService;
  @MockBean
  private JwtService jwtService;
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
      @DisplayName("応募者を全件取得できる")
      void findAllTheResumes() {
        // given
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").build();
        Resume resume2 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .education("2020年 B大学卒業")
            .experience("コンビニバイト").skills("TOEIC 900点").interests("ベンチャー企業")
            .urls("https://imageB.png").build();
        Resume resume3 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .education("2019年 C大学卒業")
            .experience("カフェバイト").skills("英検2級").interests("大手企業")
            .urls("https://imageC.png").build();
        when(resumeService.findAll())
            .thenReturn(Flux.just(resume3, resume2, resume1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes")
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
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            "2019年 C大学卒業", "カフェバイト", "英検2級",
                            "大手企業", "https://imageC.png"),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                            "2020年 B大学卒業", "コンビニバイト", "TOEIC 900点",
                            "ベンチャー企業", "https://imageB.png"),
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                            "外資企業", "https://imageA.png")
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
      @DisplayName("応募者を1件取得できる")
      void canFindTheResume() {
        // given
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").build();
        when(resumeService.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(
            Mono.just(resume1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes/12345678-1234-1234-1234-123456789abc")
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
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                        "居酒屋バイトリーダー",
                        "英検1級", "外資企業", "https://imageA.png")
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
        // given
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").build();
        when(resumeService.findByApplicantId(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(
            Flux.just(resume1));
        when(reactiveContextService.getCurrentApplicant(any(ServerWebExchange.class)))
            .thenReturn(
                Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                    .build());
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes/applicant")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getUuid, Resume::getApplicantUuid,
                        Resume::getEducation, Resume::getExperience, Resume::getSkills,
                        Resume::getInterests, Resume::getUrls)
                    .containsExactly(
                        tuple(null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                            "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                            "外資企業", "https://imageA.png")
                    )
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
      @DisplayName("応募者を登録できる")
      void canSaveTheResume() {
        // given
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").build();
        when(resumeService.insert(any(Resume.class)))
            .thenReturn(Mono.just(resume1));
        when(reactiveContextService.getCurrentApplicant(any(ServerWebExchange.class)))
            .thenReturn(
                Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
                    .build());
        // when, then
        webTestClient.post()
            .uri("/api/v1/resumes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "applicantId": "12345678-1234-1234-1234-123456789abc",
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
                    .extracting(Resume::getId, Resume::getUuid, Resume::getApplicantUuid,
                        Resume::getEducation, Resume::getExperience, Resume::getSkills,
                        Resume::getInterests, Resume::getUrls)
                    .containsExactly(
                        null, UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                        UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                        "居酒屋バイトリーダー", "英検1級", "外資企業",
                        "https://imageA.png")
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
      void canDeleteTheResume() {
        // given
        when(resumeService.deleteById(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when, then
        webTestClient.delete()
            .uri("/api/v1/resumes/12345678-1234-1234-1234-123456789abc")
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }
  }
}