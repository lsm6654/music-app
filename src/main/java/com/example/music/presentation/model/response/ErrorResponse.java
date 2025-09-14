package com.example.music.presentation.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private String timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private List<FieldError> errors;

  /**
   * 단순 에러 응답 생성
   */
  public static ErrorResponse of(int status, String error, String message, String path) {
    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now().toString())
      .status(status)
      .error(error)
      .message(message)
      .path(path)
      .build();
  }

  /**
   * 검증 에러 응답 생성
   */
  public static ErrorResponse ofValidation(int status, String error, String message, String path, List<FieldError> errors) {
    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now().toString())
      .status(status)
      .error(error)
      .message(message)
      .path(path)
      .errors(errors)
      .build();
  }

  /**
   * 필드 에러 정보
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class FieldError {
    private String field;
    private Object rejectedValue;
    private String message;
  }

}
