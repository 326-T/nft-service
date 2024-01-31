package org.example.web.request;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Company;

@Data
@NoArgsConstructor
public class CompanyInsertRequest {

  private String name;
  private String email;
  private String password;
  private String phone;
  private String address;

  public Company exportEntity() {
    return Company.builder()
        .uuid(UUID.randomUUID())
        .name(name)
        .email(email)
        .passwordDigest(password)
        .phone(phone)
        .address(address)
        .build();
  }
}
