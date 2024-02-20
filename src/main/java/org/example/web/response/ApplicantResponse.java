package org.example.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.persistence.entity.Applicant;

@Getter
public class ApplicantResponse {

  private final UUID uuid;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String phone;
  private final String address;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime updatedAt;
  private final Long version;

  public ApplicantResponse(Applicant applicant) {
    this.uuid = applicant.getUuid();
    this.firstName = applicant.getFirstName();
    this.lastName = applicant.getLastName();
    this.email = applicant.getEmail();
    this.phone = applicant.getPhone();
    this.address = applicant.getAddress();
    this.createdAt = applicant.getCreatedAt();
    this.updatedAt = applicant.getUpdatedAt();
    this.version = applicant.getVersion();
  }
}
