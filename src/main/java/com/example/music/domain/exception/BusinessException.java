package com.example.music.domain.exception;

public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
