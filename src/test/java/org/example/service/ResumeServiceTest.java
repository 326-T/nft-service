package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.example.persistence.entity.Resume;
import org.example.persistence.repository.ResumeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class ResumeServiceTest {

  @InjectMocks
  private ResumeService resumeService;
  @Mock
  private ResumeRepository resumeRepository;

  @Nested
  class FindAll {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("全件取得できる")
      void canFindAll() {
        // given
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        Resume resume2 = Resume.builder().id("2").applicantId("2").education("2020年 B大学卒業")
            .experience("コンビニバイト").skills("TOEIC 900点").interests("ベンチャー企業")
            .references("https://imageB.png").build();
        Resume resume3 = Resume.builder().id("3").applicantId("3").education("2019年 C大学卒業")
            .experience("カフェバイト").skills("英検2級").interests("大手企業")
            .references("https://imageC.png").build();
        when(resumeRepository.findAll(any(Sort.class)))
            .thenReturn(Flux.just(resume3, resume2, resume1));
        // when
        Flux<Resume> actual = resumeService.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getReferences)
                .containsExactly("3", "3", "2019年 C大学卒業", "カフェバイト", "英検2級",
                    "大手企業", "https://imageC.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getReferences)
                .containsExactly("2", "2", "2020年 B大学卒業", "コンビニバイト", "TOEIC 900点",
                    "ベンチャー企業", "https://imageB.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getReferences)
                .containsExactly("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("IDで検索できる")
      void canFindById() {
        // given
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        when(resumeRepository.findById("1")).thenReturn(Mono.just(resume1));
        // when
        Mono<Resume> actual = resumeService.findById("1");
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getReferences)
                .containsExactly("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindByApplicantId {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("applicantIdで検索できる")
      void canFindByApplicantId() {
        // given
        Resume resume1 = Resume.builder().id("1").applicantId("1").education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .references("https://imageA.png").build();
        when(resumeRepository.findByApplicantId("1")).thenReturn(Flux.just(resume1));
        // when
        Flux<Resume> actual = resumeService.findByApplicantId("1");
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getId, Resume::getApplicantId, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getReferences)
                .containsExactly("1", "1", "2021年 A大学卒業", "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class Save {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("保存できる")
      void canSave() {
        // given
        Resume resume = Resume.builder().applicantId("4").education("2018年 D大学卒業")
            .experience("飲食店バイト").skills("英検3級").interests("中小企業")
            .references("https://imageD.png").build();
        when(resumeRepository.save(resume)).thenReturn(Mono.just(resume));
        // when
        Mono<Resume> actual = resumeService.save(resume);
        // then
        StepVerifier.create(actual)
            .assertNext(resume1 -> assertThat(resume1)
                .extracting(Resume::getApplicantId, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getReferences)
                .containsExactly("4", "2018年 D大学卒業", "飲食店バイト", "英検3級",
                    "中小企業", "https://imageD.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class DeleteById {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("IDで削除できる")
      void canDeleteById() {
        // given
        when(resumeRepository.deleteById("1")).thenReturn(Mono.empty());
        // when
        Mono<Void> actual = resumeService.deleteById("1");
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}