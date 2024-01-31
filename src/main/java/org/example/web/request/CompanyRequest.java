package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Company;

@Data
@NoArgsConstructor
public class CompanyRequest {

  private String id;
  private String name;
  private String email;
  private String password;
  private String phone;
  private String address;

  public Company exportEntity() {
    return Company.builder()
        .id(id)
        .name(name)
        .email(email)
        .passwordDigest(password)
        .phone(phone)
        .address(address)
        .build();
  }
}
