package com.example.music.presentation.model.request;

import com.example.music.domain.enums.AnalyticsDimension;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "연도 및 아티스트별 앨범 분석 요청 DTO")
public class AlbumAnnualAnalyticsRequest {

  @NotNull(message = "Dimension은 필수입니다")
  @Schema(
    description = "분석 차원 (YEAR: 연도별, ARTIST_ID: 아티스트별)",
    defaultValue = "YEAR",
    requiredMode = Schema.RequiredMode.REQUIRED,
    implementation = AnalyticsDimension.class
  )
  private AnalyticsDimension dimension;

  @NotNull(message = "Dimension key는 필수입니다")
  @Min(value = 1, message = "Dimension key는 1 이상이어야 합니다")
  @Schema(
    description = "조회 대상 키 (YEAR일 경우 연도, ARTIST_ID일 경우 아티스트 ID)",
    example = "2024",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  private Integer dimensionKey;

  @Min(value = 0, message = "페이지는 0 이상이어야 합니다")
  @Builder.Default
  @Schema(
    description = "페이지 번호 (0부터 시작)",
    example = "0",
    defaultValue = "0",
    minimum = "0"
  )
  private int page = 0;

  @Min(value = 5, message = "페이지 사이즈는 5개 이상이어야 합니다")
  @Max(value = 100, message = "페이지 사이즈는 100개 이하여야 합니다")
  @Schema(
    description = "페이지 크기",
    example = "20",
    defaultValue = "20",
    minimum = "5",
    maximum = "100"
  )
  @Builder.Default
  private int size = 20;

  public Pageable toPageable() {
    return PageRequest.of(this.page, this.size);
  }

}
