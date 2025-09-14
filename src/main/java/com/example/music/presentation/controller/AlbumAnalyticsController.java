package com.example.music.presentation.controller;

import com.example.music.application.service.AlbumAnalyticsService;
import com.example.music.presentation.model.request.AlbumAnnualAnalyticsRequest;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/albums/analytics")
@RequiredArgsConstructor
@Tag(name = "Album Analytics API", description = "앨범 분석 관련 API")
public class AlbumAnalyticsController {

  private final AlbumAnalyticsService albumAnalyticsService;

  @Operation(
    summary = "연도/아티스트별 앨범 분석 조회",
    description = "특정 연도의 아티스트별 또는 특정 아티스트의 연도별 앨범 발매 현황을 조회합니다.",
    parameters = {
      @Parameter(name = "dimension", description = "분석 차원 (YEAR: 연도별, ARTIST_ID: 아티스트별)", required = true, example = "YEAR"),
      @Parameter(name = "dimensionKey", description = "조회 대상 키 (YEAR일 경우 연도, ARTIST_ID일 경우 아티스트 ID)", required = true, example = "2024"),
      @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
      @Parameter(name = "size", description = "페이지 크기", example = "20")
    }
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "성공적으로 조회됨",
      content = @Content(schema = @Schema(implementation = AlbumAnnualAnalyticsResponse.class))
    )
  })
  @GetMapping("/annual")
  public Mono<ResponseEntity<AlbumAnnualAnalyticsResponse>> getAnnualAnalytics(
    @ParameterObject @Valid @ModelAttribute AlbumAnnualAnalyticsRequest request
  ) {
    log.info("Received annual analytics request: {}", request);

    return albumAnalyticsService.getAnnualAnalytics(request)
      .map(ResponseEntity::ok)
      .doOnSuccess(response ->
        log.info("Successfully retrieved annual analytics for request: {}", request)
      )
      .doOnError(error -> log.error("Failed to retrieve annual analytics", error));
  }

}
