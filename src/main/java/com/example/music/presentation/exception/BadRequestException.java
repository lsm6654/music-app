package com.example.music.presentation.exception;

import com.example.music.domain.exception.BusinessException;
import com.example.music.domain.exception.ErrorCode;

public class BadRequestException extends BusinessException {

  public BadRequestException(String message) {
    super(message, ErrorCode.BAD_REQUEST);
  }

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode);
  }

  public BadRequestException(String message, ErrorCode errorCode) {
    super(message, errorCode);
  }

}
