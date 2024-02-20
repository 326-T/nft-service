package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Applicant;

@Data
@NoArgsConstructor
public class ApplicantUpdateRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private String address;

  public Applicant exportEntity() {
    return Applicant.builder()
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .phone(phone)
        .address(address)
        .build();
  }
}
