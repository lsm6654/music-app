package com.example.music.presentation.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이징 정보")
public class PageableResponse {

  @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
  private int pageNumber;
  
  @Schema(description = "페이지 크기", example = "20")
  private int pageSize;
  
  @Schema(description = "전체 데이터 개수", example = "100")
  private long totalElements;
  
  @Schema(description = "전체 페이지 수", example = "5")
  private int totalPages;

}
