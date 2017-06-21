package com.jszczygiel.foundation.exceptions;

public class CannotRetryException extends Exception {

  public CannotRetryException(String message) {
    super(message);
  }

  public CannotRetryException(String message, Throwable cause) {
    super(message, cause);
  }
}
