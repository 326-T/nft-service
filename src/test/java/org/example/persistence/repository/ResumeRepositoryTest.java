package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.example.listener.FlywayTestExecutionListener;
import org.example.persistence.entity.Resume;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
class ResumeRepositoryTest {

  @Autowired
  ResumeRepository resumeRepository;
  @Autowired
  ApplicantRepository applicantRepository;

  @Nested
  class findAll {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("全件取得できること")
      void findAll() {
        // when
        Flux<Resume> actual = resumeRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abe"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abe"), "2021年 A大学卒業",
                    "居酒屋バイトリーダー", "英検1級", "外資企業", "https://imageA.png", "3.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abd"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abd"), "2020年 B大学卒業",
                    "コンビニバイト", "TOEIC 900点", "ベンチャー企業", "https://imageB.png",
                    "2.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業", "https://imageC.png",
                    "1.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class findById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで検索できること")
      void findById() {
        // when
        Mono<Resume> actual = resumeRepository.findByUuid(
            UUID.fromString("12345678-1234-5678-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                    "https://imageC.png", "1.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class findByApplicantId {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("applicantIdで検索できること")
      void findByApplicantId() {
        // when
        Flux<Resume> actual = resumeRepository.findByApplicantUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                    "https://imageC.png", "1.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindByMintStatusId {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("mintStatusIdで検索できること")
      void findByMintStatusId() {
        // when
        Flux<Resume> actual = resumeRepository.findByMintStatusId(0);
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture, Resume::getMintStatusId)
                .containsExactly(UUID.fromString("12345678-1234-5678-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                    "https://imageC.png", "1.png", 0))
            .verifyComplete();
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class save {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("保存できること")
      void save() {
        // given
        Resume resume = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abf"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .education("2018年 D大学卒業").experience("飲食店バイト").skills("英検3級")
            .interests("中小企業").urls("https://imageD.png").picture("4.png").build();
        // when
        Mono<Resume> actual = resumeRepository.save(resume);
        // then
        StepVerifier.create(actual)
            .assertNext(actualResume -> assertThat(actualResume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests, Resume::getUrls,
                    Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abf"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abd"), "2018年 D大学卒業",
                    "飲食店バイト", "英検3級", "中小企業", "https://imageD.png", "4.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  @TestExecutionListeners(
      listeners = {FlywayTestExecutionListener.class},
      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
  class deleteById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで削除できること")
      void deleteById() {
        // when
        Mono<Void> actual = resumeRepository.deleteByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}