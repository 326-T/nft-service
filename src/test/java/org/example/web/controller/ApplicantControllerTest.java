package org.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.example.persistence.entity.Applicant;
import org.example.service.ApplicantService;
import org.example.service.JwtService;
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
    controllers = ApplicantController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {AuthenticationWebFilter.class})})
@AutoConfigureWebTestClient
class ApplicantControllerTest {

  @MockBean
  private ApplicantService applicantService;
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
      void findAllTheApplicants() {
        // given
        Applicant applicant1 = Applicant.builder().id("1").firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        Applicant applicant2 = Applicant.builder().id("2").firstName("次郎").lastName("鈴木")
            .email("yyy@example.org").phone("090-9876-5432").address("東京都新宿区")
            .passwordDigest("").build();
        Applicant applicant3 = Applicant.builder().id("3").firstName("三郎").lastName("佐藤")
            .email("zzz@example.org").phone("090-1111-2222").address("東京都千代田区")
            .passwordDigest("").build();
        when(applicantService.findAll())
            .thenReturn(Flux.just(applicant3, applicant2, applicant1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/applicants")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(Applicant.class)
            .hasSize(3)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getId, Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly(
                        tuple("3", "三郎", "佐藤", "zzz@example.org", "090-1111-2222",
                            "東京都千代田区", ""),
                        tuple("2", "次郎", "鈴木", "yyy@example.org", "090-9876-5432",
                            "東京都新宿区", ""),
                        tuple("1", "太郎", "山田", "xxx@example.org", "090-1234-5678",
                            "東京都渋谷区", "")
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
      void canFindTheApplicant() {
        // given
        Applicant applicant1 = Applicant.builder().id("1").firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(applicantService.findById("1")).thenReturn(Mono.just(applicant1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/applicants/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Applicant.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getId, Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly(
                        "1", "太郎", "山田", "xxx@example.org", "090-1234-5678",
                        "東京都渋谷区", ""
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
      void canSaveTheApplicant() {
        // given
        Applicant applicant1 = Applicant.builder().id("1").firstName("四郎").lastName("田中")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("password_digest").build();
        when(applicantService.save(any(Applicant.class), eq("password")))
            .thenReturn(Mono.just(applicant1));
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
                """
            )
            .exchange()
            .expectStatus().isOk()
            .expectBody(Applicant.class)
            .consumeWith(result ->
                assertThat(result.getResponseBody())
                    .extracting(Applicant::getId, Applicant::getFirstName, Applicant::getLastName,
                        Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                        Applicant::getPasswordDigest)
                    .containsExactly("1", "四郎", "田中", "aaa@example.org", "090-3333-4444",
                        "東京都港区", "password_digest")
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
        // given
        Applicant applicant1 = Applicant.builder().id("1").firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区").build();
        when(applicantService.login("xxx@example.org", "password"))
            .thenReturn(Mono.just(applicant1));
        when(jwtService.encode(applicant1)).thenReturn("jwt");
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
            .expectCookie().valueEquals("token", "jwt");
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
        // given
        when(applicantService.deleteById("1")).thenReturn(Mono.empty());
        // when, then
        webTestClient.delete()
            .uri("/api/v1/applicants/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }
  }
}