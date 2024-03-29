package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Resume;

@Data
@NoArgsConstructor
public class ResumeUpdateRequest {

  private String education;
  private String experience;
  private String skills;
  private String interests;
  private String urls;
  private String picture;
  private Float minimumPrice;

  public Resume exportEntity() {
    return Resume.builder()
        .education(education)
        .experience(experience)
        .skills(skills)
        .interests(interests)
        .urls(urls)
        .picture(picture)
        .minimumPrice(minimumPrice)
        .build();
  }
}
