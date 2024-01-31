package org.example.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.UUID;
import org.example.persistence.entity.Applicant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final String secretKey;
  private final Long ttl;

  public JwtService(
      @Value("${jwt.secret-key}") String secretKey,
      @Value("${jwt.ttl}") Long ttl) {
    this.secretKey = secretKey;
    this.ttl = ttl;
  }

  public String encode(Applicant applicant) {
    Date now = new Date();
    return JWT.create()
        .withJWTId(UUID.randomUUID().toString())
        .withIssuer("org.example")
        .withAudience("org.example")
        .withSubject(applicant.getId())
        .withClaim("firstName", applicant.getFirstName())
        .withClaim("lastName", applicant.getLastName())
        .withClaim("email", applicant.getEmail())
        .withClaim("phone", applicant.getPhone())
        .withClaim("address", applicant.getAddress())
        .withIssuedAt(now)
        .withNotBefore(now)
        .withExpiresAt(new Date(now.getTime() + ttl))
        .sign(Algorithm.HMAC256(secretKey));
  }

  public Applicant decode(String jwt)
      throws TokenExpiredException, SignatureVerificationException, IllegalArgumentException {
    DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secretKey)).build().verify(jwt);
    return Applicant.builder()
        .id(decodedJWT.getSubject())
        .firstName(decodedJWT.getClaim("firstName").asString())
        .lastName(decodedJWT.getClaim("lastName").asString())
        .email(decodedJWT.getClaim("email").asString())
        .phone(decodedJWT.getClaim("phone").asString())
        .address(decodedJWT.getClaim("address").asString())
        .build();
  }
}
