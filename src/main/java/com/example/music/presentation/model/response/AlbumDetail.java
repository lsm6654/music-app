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
@Schema(description = "앨범 상세 정보 (dimension에 따라 다른 필드가 채워짐)")
public class AlbumDetail {

  @Schema(description = "아티스트 ID (dimension=year의 details에서 사용)", example = "123")
  private Long artistId;
  
  @Schema(description = "아티스트 이름 (dimension=year의 details에서 사용)", example = "Dan Bull")
  private String artistName;
  
  @Schema(description = "연도 (dimension=artistId의 details에서 사용)", example = "2024")
  private Short year;
  
  @Schema(description = "앨범 수", example = "5")
  private Integer albumCount;

}
