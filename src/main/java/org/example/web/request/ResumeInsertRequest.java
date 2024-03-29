package org.example.web.request;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Resume;

@Data
@NoArgsConstructor
public class ResumeInsertRequest {

  private String education;
  private String experience;
  private String skills;
  private String interests;
  private String urls;
  private String picture;

  public Resume exportEntity() {
    return Resume.builder()
        .uuid(UUID.randomUUID())
        .education(education)
        .experience(experience)
        .skills(skills)
        .interests(interests)
        .urls(urls)
        .picture(picture)
        .build();
  }
}
