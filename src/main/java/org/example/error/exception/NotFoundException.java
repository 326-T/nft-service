package org.example.error.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

  private final String detail;

  public NotFoundException(String message) {
    super(message);
    detail = "%s.%s".formatted(Thread.currentThread().getStackTrace()[2].getClassName(),
        Thread.currentThread().getStackTrace()[2].getMethodName());
  }
}
