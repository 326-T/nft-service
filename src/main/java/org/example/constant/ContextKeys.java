package org.example.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContextKeys {
  APPLICANT_KEY("applicant"),
  COMPANY_KEY("company");

  private final String key;
}
