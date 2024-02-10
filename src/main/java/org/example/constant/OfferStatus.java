package org.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OfferStatus {
  PENDING(0),
  ACCEPTED(1),
  REJECTED(2);

  private final Integer id;
}
