package com.example.music.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  BAD_REQUEST(HttpStatus.BAD_REQUEST, "001", "잘못된 입력값입니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "002", "서버 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}
