package com.example.music.presentation.model.response;

import com.example.music.domain.enums.AnalyticsDimension;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "앨범 분석 응답 DTO")
public class AlbumAnnualAnalyticsResponse {

  @Schema(description = "분석 차원 (year 또는 artistId)")
  private AnalyticsDimension dimension;
  
  @Schema(description = "조회 대상 키 (YEAR일 경우 연도, ARTIST_ID일 경우 아티스트 ID)", example = "2024")
  private Integer dimensionKey;
  
  @Schema(description = "조회 대상 이름 (연도 또는 아티스트명)", example = "2024 또는 Gucci Mane")
  private String dimensionName;
  
  @Schema(description = "분석 결과 데이터")
  private List<AlbumAnalyticsItem> content;
  
  @Schema(description = "페이징 정보")
  private PageableResponse pageable;
  
  @Schema(description = "첫 번째 페이지 여부", example = "true")
  private boolean first;
  
  @Schema(description = "마지막 페이지 여부", example = "false")
  private boolean last;

}
