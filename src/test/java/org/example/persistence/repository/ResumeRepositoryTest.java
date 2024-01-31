package org.example.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.config.MongoAuditingConfiguration;
import org.example.persistence.entity.Resume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import(MongoAuditingConfiguration.class)
@DirtiesContext
class ResumeRepositoryTest {

  @Autowired
  ResumeRepository resumeRepository;

  @BeforeEach
  void setUp() {
    resumeRepository.save(
        Resume.builder().applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build()).block();
    resumeRepository.save(
        Resume.builder().applicantId("2").education("2020年 B大学卒業").experience("コンビニバイト")
            .skills("TOEIC 900点").interests("ベンチャー企業")
            .references("https://imageB.png").build()).block();
    resumeRepository.save(
        Resume.builder().applicantId("3").education("2019年 C大学卒業").experience("カフェバイト")
            .skills("英検2級").interests("大手企業")
            .references("https://imageC.png").build()).block();
  }

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
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("3", "2019年 C大学卒業", "カフェバイト", "英検2級", "大手企業",
                    "https://imageC.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("2", "2020年 B大学卒業", "コンビニバイト", "TOEIC 900点",
                    "ベンチャー企業", "https://imageB.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
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
        // given
        String id = resumeRepository.findByApplicantId("1").blockFirst().getId();
        // when
        Mono<Resume> actual = resumeRepository.findById(id);
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
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
        Flux<Resume> actual = resumeRepository.findByApplicantId("1");
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class save {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("保存できること")
      void save() {
        // given
        Resume resume = Resume.builder().applicantId("4").education("2018年 D大学卒業")
            .experience("飲食店バイト").skills("英検3級").interests("中小企業")
            .references("https://imageD.png").build();
        // when
        Mono<Resume> actual = resumeRepository.save(resume);
        // then
        StepVerifier.create(actual)
            .assertNext(actualResume -> assertThat(actualResume)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("4", "2018年 D大学卒業", "飲食店バイト", "英検3級", "中小企業",
                    "https://imageD.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class deleteById {

    @Nested
    @DisplayName("正常系")
    class regular {

      @Test
      @DisplayName("IDで削除できること")
      void deleteById() {
        // given
        String id = resumeRepository.findByApplicantId("1").blockFirst().getId();
        // when
        Mono<Void> actual = resumeRepository.deleteById(id);
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}