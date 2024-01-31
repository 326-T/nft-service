package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Company;

@Data
@NoArgsConstructor
public class CompanyUpdateRequest {

  private String name;
  private String email;
  private String phone;
  private String address;

  public Company exportEntity() {
    return Company.builder()
        .name(name)
        .email(email)
        .phone(phone)
        .address(address)
        .build();
  }
}
