package org.example.web.request;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Resume;

@Data
@NoArgsConstructor
public class ResumeInsertRequest {

  private UUID applicantUuid;
  private String education;
  private String experience;
  private String skills;
  private String interests;
  private String urls;

  public Resume exportEntity() {
    return Resume.builder()
        .uuid(UUID.randomUUID())
        .applicantUuid(applicantUuid)
        .education(education)
        .experience(experience)
        .skills(skills)
        .interests(interests)
        .urls(urls)
        .build();
  }
}
