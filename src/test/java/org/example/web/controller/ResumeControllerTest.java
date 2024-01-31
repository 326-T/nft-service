package org.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.example.persistence.entity.Resume;
import org.example.service.JwtService;
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
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        Resume resume2 = Resume.builder().id("2").applicantId("2").education("2020年 B大学卒業")
            .experience("コンビニバイト").skills("TOEIC 900点").interests("ベンチャー企業")
            .references("https://imageB.png").build();
        Resume resume3 = Resume.builder().id("3").applicantId("3").education("2019年 C大学卒業")
            .experience("カフェバイト").skills("英検2級").interests("大手企業")
            .references("https://imageC.png").build();
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
                    .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getReferences)
                    .containsExactly(
                        tuple("3", "3", "2019年 C大学卒業", "カフェバイト", "英検2級",
                            "大手企業", "https://imageC.png"),
                        tuple("2", "2", "2020年 B大学卒業", "コンビニバイト", "TOEIC 900点",
                            "ベンチャー企業", "https://imageB.png"),
                        tuple("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
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
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        when(resumeService.findById("1")).thenReturn(Mono.just(resume1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getReferences)
                    .containsExactly("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー",
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
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        when(resumeService.findByApplicantId("1")).thenReturn(Flux.just(resume1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/resumes/applicant/1")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getReferences)
                    .containsExactly(
                        tuple("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
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
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        when(resumeService.save(any(Resume.class)))
            .thenReturn(Mono.just(resume1));
        // when, then
        webTestClient.post()
            .uri("/api/v1/resumes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "applicantId": "1",
                  "education": "2021年 A大学卒業",
                  "experience": "居酒屋バイトリーダー",
                  "skills": "英検1級",
                  "interests": "外資企業",
                  "references": "https://imageA.png"
                }
                """
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(Resume.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                        Resume::getExperience, Resume::getSkills, Resume::getInterests,
                        Resume::getReferences)
                    .containsExactly(
                        "1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級", "外資企業",
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
        when(resumeService.deleteById("1")).thenReturn(Mono.empty());
        // when, then
        webTestClient.delete()
            .uri("/api/v1/resumes/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }
  }
}