package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.error.exception.PasswordAuthenticationException;
import org.example.persistence.entity.Applicant;
import org.example.persistence.repository.ApplicantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class ApplicantServiceTest {

  @InjectMocks
  private ApplicantService applicantService;
  @Mock
  private ApplicantRepository applicantRepository;
  @Mock
  private PasswordEncoder passwordEncoder;

  @Nested
  class FindAll {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("全件取得できる")
      void canFindAll() {
        // given
        Applicant applicant1 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        Applicant applicant2 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd")).firstName("次郎").lastName("鈴木")
            .email("yyy@example.org").phone("090-9876-5432").address("東京都新宿区")
            .passwordDigest("").build();
        Applicant applicant3 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abe")).firstName("三郎").lastName("佐藤")
            .email("zzz@example.org").phone("090-1111-2222").address("東京都千代田区")
            .passwordDigest("").build();
        when(applicantRepository.findAll(any(Sort.class)))
            .thenReturn(Flux.just(applicant3, applicant2, applicant1));
        // when
        Flux<Applicant> actual = applicantService.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎", "山田", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abd"), "次郎", "鈴木", "yyy@example.org", "090-9876-5432",
                    "東京都新宿区",
                    ""))
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abe"), "三郎", "佐藤", "zzz@example.org", "090-1111-2222",
                    "東京都千代田区", ""));
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
        // given
        Applicant applicant1 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(applicantRepository.findByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.just(applicant1));
        // when
        Mono<Applicant> actual = applicantService.findByUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎", "山田", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindByEmail {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("emailで検索できる")
      void canFindByEmail() {
        // given
        Applicant applicant1 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        when(applicantRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(applicant1));
        // when
        Mono<Applicant> actual = applicantService.findByEmail("xxx@example.org");
        // then
        StepVerifier.create(actual)
            .assertNext(applicant -> assertThat(applicant)
                .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎", "山田", "xxx@example.org", "090-1234-5678",
                    "東京都渋谷区",
                    ""))
            .verifyComplete();
      }
    }
  }

  @Nested
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("保存できる")
      void canSave() {
        // given
        Applicant applicant1 = Applicant.builder().firstName("四郎").lastName("田中")
            .email("aaa@example.org").phone("090-3333-4444").address("東京都港区")
            .passwordDigest("password").build();
        when(applicantRepository.save(any(Applicant.class))).thenReturn(Mono.just(applicant1));
        when(passwordEncoder.encode("password")).thenReturn("password_digest");
        // when
        Mono<Applicant> actual = applicantService.save(applicant1, "password");
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly("四郎", "田中", "aaa@example.org", "090-3333-4444", "東京都港区",
                    "password_digest"))
            .verifyComplete();
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
        Applicant applicant1 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("password_digest").build();
        when(applicantRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(applicant1));
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(true);
        // when
        Mono<Applicant> actual = applicantService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual)
            .assertNext(a -> assertThat(a)
                .extracting(Applicant::getFirstName, Applicant::getLastName,
                    Applicant::getEmail, Applicant::getPhone, Applicant::getAddress,
                    Applicant::getPasswordDigest)
                .containsExactly("太郎", "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区",
                    "password_digest"))
            .verifyComplete();
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("emailが間違っている")
      void emailIsWrong() {
        // given
        when(applicantRepository.findByEmail("xxx@example.org")).thenReturn(Mono.empty());
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(true);
        // when
        Mono<Applicant> actual = applicantService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual).expectError(PasswordAuthenticationException.class).verify();
      }

      @Test
      @DisplayName("passwordが間違っている")
      void passwordIsWrong() {
        // given
        Applicant applicant1 = Applicant.builder().uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎").lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("password_digest").build();
        when(applicantRepository.findByEmail("xxx@example.org")).thenReturn(Mono.just(applicant1));
        when(passwordEncoder.matches("password", "password_digest")).thenReturn(false);
        // when
        Mono<Applicant> actual = applicantService.login("xxx@example.org", "password");
        // then
        StepVerifier.create(actual).expectError(PasswordAuthenticationException.class).verify();
      }
    }
  }

  @Nested
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("IDで削除できる")
      void canDeleteById() {
        // given
        when(applicantRepository.deleteById(UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when
        Mono<Void> actual = applicantService.deleteById(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}