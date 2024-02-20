package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.example.persistence.entity.Resume;
import org.example.persistence.repository.ResumeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
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
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").picture("3.png").build();
        Resume resume2 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abd"))
            .education("2020年 B大学卒業")
            .experience("コンビニバイト").skills("TOEIC 900点").interests("ベンチャー企業")
            .urls("https://imageB.png").picture("2.png").build();
        Resume resume3 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abe"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abe"))
            .education("2019年 C大学卒業")
            .experience("カフェバイト").skills("英検2級").interests("大手企業")
            .urls("https://imageC.png").picture("1.png").build();
        when(resumeRepository.findAll(any(Sort.class)))
            .thenReturn(Flux.just(resume3, resume2, resume1));
        // when
        Flux<Resume> actual = resumeService.findAll();
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abe"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abe"), "2019年 C大学卒業",
                    "カフェバイト", "英検2級",
                    "大手企業", "https://imageC.png", "1.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abd"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abd"), "2020年 B大学卒業",
                    "コンビニバイト", "TOEIC 900点",
                    "ベンチャー企業", "https://imageB.png", "2.png"))
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                    "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png", "3.png"))
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
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").picture("3.png").build();
        when(resumeRepository.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(
            Mono.just(resume1));
        // when
        Mono<Resume> actual = resumeService.findByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                    "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png", "3.png"))
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
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").picture("3.png").build();
        when(resumeRepository.findByApplicantUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(
            Flux.just(resume1));
        // when
        Flux<Resume> actual = resumeService.findByApplicantId(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                    "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png", "3.png"))
            .verifyComplete();
      }
    }
  }

  @Nested
  class FindByMintStatusId {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("mintStatusIdで検索できる")
      void canFindByMintStatusId() {
        // given
        Resume resume1 = Resume.builder()
            .uuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abc"))
            .education("2021年 A大学卒業")
            .experience("居酒屋バイトリーダー").skills("英検1級").interests("外資企業")
            .urls("https://imageA.png").picture("3.png").mintStatusId(0).build();
        when(resumeRepository.findByMintStatusId(0)).thenReturn(
            Flux.just(resume1));
        // when
        Flux<Resume> actual = resumeService.findByMintStatusId(0);
        // then
        StepVerifier.create(actual)
            .assertNext(resume -> assertThat(resume)
                .extracting(Resume::getUuid, Resume::getApplicantUuid, Resume::getEducation,
                    Resume::getExperience, Resume::getSkills, Resume::getInterests,
                    Resume::getUrls, Resume::getPicture, Resume::getMintStatusId)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                    UUID.fromString("12345678-1234-1234-1234-123456789abc"), "2021年 A大学卒業",
                    "居酒屋バイトリーダー", "英検1級",
                    "外資企業", "https://imageA.png", "3.png", 0))
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
        Resume resume = Resume.builder()
            .applicantUuid(UUID.fromString("12345678-1234-1234-1234-123456789abf"))
            .education("2018年 D大学卒業")
            .experience("飲食店バイト").skills("英検3級").interests("中小企業")
            .urls("https://imageD.png").picture("4.png").build();
        when(resumeRepository.save(resume)).thenReturn(Mono.just(resume));
        // when
        Mono<Resume> actual = resumeService.insert(resume);
        // then
        StepVerifier.create(actual)
            .assertNext(resume1 -> assertThat(resume1)
                .extracting(Resume::getApplicantUuid, Resume::getEducation, Resume::getExperience,
                    Resume::getSkills, Resume::getInterests, Resume::getUrls, Resume::getPicture)
                .containsExactly(UUID.fromString("12345678-1234-1234-1234-123456789abf"),
                    "2018年 D大学卒業", "飲食店バイト", "英検3級",
                    "中小企業", "https://imageD.png", "4.png"))
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
        when(resumeRepository.deleteByUuid(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"))).thenReturn(Mono.empty());
        // when
        Mono<Void> actual = resumeService.deleteById(
            UUID.fromString("12345678-1234-1234-1234-123456789abc"));
        // then
        StepVerifier.create(actual).verifyComplete();
      }
    }
  }
}