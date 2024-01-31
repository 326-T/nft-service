package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Applicant;

@Data
@NoArgsConstructor
public class ApplicantLoginRequest {

  private String email;
  private String password;

  public Applicant exportEntity() {
    return Applicant.builder()
        .email(email)
        .passwordDigest(password)
        .build();
  }
}
