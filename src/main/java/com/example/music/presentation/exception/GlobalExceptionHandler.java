package com.example.music.presentation.exception;

import com.example.music.domain.exception.BusinessException;
import com.example.music.domain.exception.ErrorCode;
import com.example.music.presentation.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleBusinessException(BusinessException e, ServerHttpRequest request) {
    log.error("Business exception occurred: {}", e.getMessage(), e);

    ErrorCode errorCode = e.getErrorCode();
    ErrorResponse errorResponse = ErrorResponse.of(
      errorCode.getStatus().value(),
      errorCode.getCode(),
      e.getMessage(),
      request.getPath().value()
    );

    return Mono.just(ResponseEntity.status(errorCode.getStatus()).body(errorResponse));
  }

  @ExceptionHandler({
    WebExchangeBindException.class,
    MethodArgumentTypeMismatchException.class,
    IllegalArgumentException.class,
    ServerWebInputException.class,
    ConversionFailedException.class
  })
  public Mono<ResponseEntity<ErrorResponse>> handleBadRequestException(Exception e, ServerHttpRequest request) {
    log.error("Bad request error: {} - {}", e.getClass().getSimpleName(), e.getMessage());

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.BAD_REQUEST.value(),
      ErrorCode.BAD_REQUEST.getCode(),
      "잘못된 요청입니다.",
      request.getPath().value()
    );

    return Mono.just(ResponseEntity.badRequest().body(errorResponse));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception e, ServerHttpRequest request) {
    log.error("Unexpected error occurred", e);

    ErrorResponse errorResponse = ErrorResponse.of(
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
      "예상치 못한 오류가 발생했습니다.",
      request.getPath().value()
    );

    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
  }

}
