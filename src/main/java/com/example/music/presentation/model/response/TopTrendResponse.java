package com.example.music.presentation.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "인기 곡 트렌드 응답")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopTrendResponse {

  @Schema(description = "응답 생성 시간")
  private LocalDateTime timestamp;
  
  @Schema(description = "트렌드 집계 기간 정보")
  private TrendPeriod period;
  
  @Schema(description = "트렌드 곡 목록")
  private List<TrendItem> trends;
  
  @Schema(description = "전체 곡 개수")
  private int totalSongs;

  @Schema(description = "트렌드 집계 기간")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TrendPeriod {
    
    @Schema(description = "집계 시작 시간")
    private LocalDateTime start;
    
    @Schema(description = "집계 종료 시간")
    private LocalDateTime end;
  }

  @Schema(description = "개별 트렌드 곡 정보")
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TrendItem {
    
    @Schema(description = "순위")
    private int rank;
    
    @Schema(description = "곡 ID")
    private String songId;
    
    @Schema(description = "좋아요 수")
    private int likeCount;
  }

}
