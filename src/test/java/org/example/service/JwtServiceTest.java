package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.example.config.JwtConfig;
import org.example.persistence.entity.Applicant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtServiceTest {

  @InjectMocks
  private JwtService jwtService;
  @Mock
private JwtConfig jwtConfig;

  @BeforeAll
  void beforeAll() {
    when(jwtConfig.getTtl()).thenReturn(1000L);
    when(jwtConfig.getSecretKey()).thenReturn("secret");
  }

  @Nested
  class encodeApplicant {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("JWTを生成できる")
      void encode() {
        // given
        Applicant user = Applicant.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc")).firstName("太郎")
            .lastName("山田")
            .email("xxx@example.org").phone("090-1234-5678").address("東京都渋谷区")
            .passwordDigest("").build();
        // when
        String jwt = jwtService.encodeApplicant(user);
        // then
        DecodedJWT jwtDecoded = JWT.require(Algorithm.HMAC256("secret")).build().verify(jwt);
        assertThat(jwtDecoded.getIssuer()).isEqualTo("org.example");
        assertThat(jwtDecoded.getAudience()).isEqualTo(List.of("org.example"));
        assertThat(jwtDecoded.getSubject()).isEqualTo("12345678-1234-1234-1234-123456789abc");
        assertThat(jwtDecoded.getClaim("firstName").asString()).isEqualTo("太郎");
        assertThat(jwtDecoded.getClaim("lastName").asString()).isEqualTo("山田");
        assertThat(jwtDecoded.getClaim("email").asString()).isEqualTo("xxx@example.org");
        assertThat(jwtDecoded.getClaim("phone").asString()).isEqualTo("090-1234-5678");
        assertThat(jwtDecoded.getClaim("address").asString()).isEqualTo("東京都渋谷区");
      }
    }
  }

  @Nested
  class decodeApplicant {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("JWTをデコードできる")
      void decode() {
        // given
        Date now = new Date();
        String jwt = JWT.create()
            .withJWTId(UUID.randomUUID().toString())
            .withIssuer("org.example")
            .withAudience("org.example")
            .withSubject("12345678-1234-1234-1234-123456789abc")
            .withClaim("firstName", "太郎")
            .withClaim("lastName", "山田")
            .withClaim("email", "xxx@example.org")
            .withClaim("phone", "090-1234-5678")
            .withClaim("address", "東京都渋谷区")
            .withIssuedAt(now)
            .withNotBefore(now)
            .withExpiresAt(new Date(now.getTime() + 1000L))
            .sign(Algorithm.HMAC256("secret"));
        // when
        Applicant applicant = jwtService.decodeApplicant(jwt);
        // then
        assertThat(applicant)
            .extracting(Applicant::getUuid, Applicant::getFirstName, Applicant::getLastName,
                Applicant::getEmail,
                Applicant::getPhone, Applicant::getAddress)
            .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"), "太郎",
                "山田", "xxx@example.org", "090-1234-5678", "東京都渋谷区");
      }
    }

    @Nested
    @DisplayName("異常系")
    class Error {

      @Test
      @DisplayName("JWTの署名が不正な場合は例外が発生する")
      void differentKey() {
        // given
        Date now = new Date();
        String jwt = JWT.create()
            .withJWTId(UUID.randomUUID().toString())
            .withIssuer("org.example")
            .withAudience("org.example")
            .withSubject("1")
            .withClaim("firstName", "太郎")
            .withClaim("lastName", "山田")
            .withClaim("email", "xxx@example.org")
            .withClaim("phone", "090-1234-5678")
            .withClaim("address", "東京都渋谷区")
            .withIssuedAt(now)
            .withNotBefore(now)
            .withExpiresAt(new Date(now.getTime() + 1000L))
            .sign(Algorithm.HMAC256("invalid_secret"));
        // when, then
        assertThrows(SignatureVerificationException.class, () -> jwtService.decodeApplicant(jwt));
      }

      @Test
      @DisplayName("JWTの有効期限が切れている場合は例外が発生する")
      void expired() {
        // given
        Date now = new Date();
        String jwt = JWT.create()
            .withJWTId(UUID.randomUUID().toString())
            .withIssuer("org.example")
            .withAudience("org.example")
            .withSubject("1")
            .withClaim("firstName", "太郎")
            .withClaim("lastName", "山田")
            .withClaim("email", "xxx@example.org")
            .withClaim("phone", "090-1234-5678")
            .withClaim("address", "東京都渋谷区")
            .withIssuedAt(now)
            .withNotBefore(now)
            .withExpiresAt(new Date(now.getTime() - 1000L))
            .sign(Algorithm.HMAC256("secret"));
        // when, then
        assertThrows(TokenExpiredException.class, () -> jwtService.decodeApplicant(jwt));
      }

      @Test
      void notJwtCase() {
        // when, then
        assertThrows(JWTDecodeException.class, () -> jwtService.decodeApplicant("not_jwt"));
      }
    }
  }
}