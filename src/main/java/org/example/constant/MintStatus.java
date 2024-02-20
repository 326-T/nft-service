package org.example.constant;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MintStatus {
  PENDING(0, "未ミント"),
  PUBLISHED(1, "ミント済"),
  EXPIRED(2, "期限切れ");

  private final Integer id;
  private final String name;

  public static MintStatus valueOf(Integer id) {
    return Arrays.stream(MintStatus.values())
        .filter(status -> status.getId().equals(id))
        .findFirst().orElse(PENDING);
  }
}
