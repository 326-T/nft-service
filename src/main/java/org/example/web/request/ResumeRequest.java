package org.example.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.persistence.entity.Resume;

@Data
@NoArgsConstructor
public class ResumeRequest {

  private String id;
  private String applicantId;
  private String education;
  private String experience;
  private String skills;
  private String interests;
  private String references;

  public Resume exportEntity() {
    return Resume.builder()
        .id(id)
        .applicantId(applicantId)
        .education(education)
        .experience(experience)
        .skills(skills)
        .interests(interests)
        .references(references)
        .build();
  }
}
