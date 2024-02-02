package org.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.persistence.entity.Company;
import org.example.service.Base64Service;
import org.example.service.CompanyService;
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
    controllers = CompanyController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {AuthenticationWebFilter.class})})
@AutoConfigureWebTestClient
class CompanyControllerTest {

  @MockBean
  private CompanyService applicantService;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private Base64Service base64Service;
  @Autowired
  private WebTestClient webTestClient;

  @Nested
  class Index {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("企業を全件取得できる")
      void findAllTheCompanies() {
        // given
        Company applicant1 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        Company applicant2 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd")).name("B株式会社")
            .email("yyy@example.org").phone("090-9876-5432").address("東京都新宿区")
            .passwordDigest("").build();
        Company applicant3 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abe")).name("C株式会社")
            .email("zzz@example.org").phone("090-1111-2222").address("東京都千代田区")
            .passwordDigest("").build();
        when(applicantService.findAll())
            .thenReturn(Flux.just(applicant3, applicant2, applicant1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/companies")
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
  }

  @Nested
  class FindById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("企業を1件取得できる")
      void canFindTheCompany() {
        // given
        Company applicant1 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(applicantService.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(
            Mono.just(applicant1));
        // when, then
        webTestClient.get()
            .uri("/api/v1/companies/12345678-1234-1234-1234-123456789abc")
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
  }

  @Nested
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("企業を登録できる")
      void canSaveTheCompany() {
        // given
        Company applicant1 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).name("D株式会社")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("password_digest").build();
        when(applicantService.save(any(Company.class), eq("password")))
            .thenReturn(Mono.just(applicant1));
        when(jwtService.encodeCompany(any(Company.class))).thenReturn("jwt");
        when(base64Service.encode("jwt")).thenReturn("base64");
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
                """
            )
            .exchange()
            .expectStatus().isOk()
            .expectCookie().valueEquals("company-token", "base64");
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
        Company applicant1 = Company.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).name("A株式会社")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区").build();
        when(applicantService.login("xxx@example.org", "password"))
            .thenReturn(Mono.just(applicant1));
        when(jwtService.encodeCompany(any(Company.class))).thenReturn("jwt");
        when(base64Service.encode("jwt")).thenReturn("base64");
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
            .expectCookie().valueEquals("company-token", "base64");
      }
    }
  }

  @Nested
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("企業を1件削除できる")
      void canDeleteTheCompany() {
        // given
        when(applicantService.deleteById(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when, then
        webTestClient.delete()
            .uri("/api/v1/companies/12345678-1234-1234-1234-123456789abc")
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
      }
    }
  }
}