package org.example.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class Base64ServiceTest {

  @InjectMocks
  private Base64Service base64Service;

  @Nested
  class Encode {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("文字列をBase64でエンコードできる")
      void canEncodeStringToBase64() {
        // given
        String raw = "test";
        // when
        String encoded = base64Service.encode(raw);
        // then
        assertThat(encoded).isEqualTo("dGVzdA==");
      }
    }
  }

  @Nested
  class Decode {

    @Nested
    @DisplayName("正常系")
    class Regular {

      @Test
      @DisplayName("Base64でエンコードされた文字列をデコードできる")
      void canDecodeBase64String() {
        // given
        String encoded = "dGVzdA==";
        // when
        String raw = base64Service.decode(encoded);
        // then
        assertThat(raw).isEqualTo("test");
      }
    }
  }
}