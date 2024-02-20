package org.example.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.persistence.entity.Company;

@Getter
public class CompanyResponse {

  private final UUID uuid;
  private final String name;
  private final String email;
  private final String phone;
  private final String address;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime updatedAt;
  private final Long version;

  public CompanyResponse(Company company) {
    this.uuid = company.getUuid();
    this.name = company.getName();
    this.email = company.getEmail();
    this.phone = company.getPhone();
    this.address = company.getAddress();
    this.createdAt = company.getCreatedAt();
    this.updatedAt = company.getUpdatedAt();
    this.version = company.getVersion();
  }
}
