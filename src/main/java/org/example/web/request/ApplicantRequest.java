package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Applicant;

@Data
@NoArgsConstructor
public class ApplicantRequest {

  private String id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phone;
  private String address;

  public Applicant exportEntity() {
    return Applicant.builder()
        .id(id)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .passwordDigest(password)
        .phone(phone)
        .address(address)
        .build();
  }
}
