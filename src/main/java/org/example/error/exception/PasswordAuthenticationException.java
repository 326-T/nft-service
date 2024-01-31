package org.example.error.exception;

import lombok.Getter;

@Getter
public class PasswordAuthenticationException extends RuntimeException {

  private final String detail;

  public PasswordAuthenticationException(String message) {
    super(message);
    detail = "%s.%s".formatted(Thread.currentThread().getStackTrace()[2].getClassName(),
        Thread.currentThread().getStackTrace()[2].getMethodName());
  }
}
