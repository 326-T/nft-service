package org.example.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.example.constant.MintStatus;
import org.example.persistence.entity.Resume;

@Getter
public class ResumeResponse {

  private final UUID uuid;
  private final UUID applicantUuid;
  private final String education;
  private final String experience;
  private final String skills;
  private final String interests;
  private final String urls;
  private final String picture;
  private final String mintStatus;
  private final Integer mintStatusId;
  private final Float minimumPrice;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime createdAt;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private final LocalDateTime updatedAt;
  private final Long version;

  public ResumeResponse(Resume resume) {
    this.uuid = resume.getUuid();
    this.applicantUuid = resume.getApplicantUuid();
    this.education = resume.getEducation();
    this.experience = resume.getExperience();
    this.skills = resume.getSkills();
    this.interests = resume.getInterests();
    this.urls = resume.getUrls();
    this.picture = resume.getPicture();
    this.mintStatus = MintStatus.valueOf(resume.getMintStatusId()).getName();
    this.mintStatusId = resume.getMintStatusId();
    this.minimumPrice = resume.getMinimumPrice();
    this.createdAt = resume.getCreatedAt();
    this.updatedAt = resume.getUpdatedAt();
    this.version = resume.getVersion();
  }
}
