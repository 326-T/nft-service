package org.example.constant;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OfferStatus {
  PENDING(0, "申請中"),
  PENDING_PAYMENT(1, "支払い待ち"),
  ACCEPTED(2, "承認済み"),
  REJECTED(3, "拒否済み");

  private final Integer id;
  private final String name;

  public static OfferStatus valueOf(Integer id) {
    return Arrays.stream(OfferStatus.values())
        .filter(status -> status.getId().equals(id))
        .findFirst().orElse(PENDING);
  }
}
