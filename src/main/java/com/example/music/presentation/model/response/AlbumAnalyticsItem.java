package com.example.music.presentation.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "앨범 분석 항목 (dimension에 따라 다른 필드가 채워짐)")
public class AlbumAnalyticsItem {

  @Schema(description = "아티스트 ID (dimension=YEAR일 때 사용)", example = "608")
  private Long artistId;
  
  @Schema(description = "아티스트 이름 (dimension=YEAR일 때 사용)", example = "Gucci Mane")
  private String artistName;
  
  @Schema(description = "연도 (dimension=ARTIST_ID일 때 사용)", example = "2024")
  private Short year;
  
  @Schema(description = "앨범 수", example = "17")
  private Integer albumCount;

}
