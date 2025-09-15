package com.example.music.presentation.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "곡 좋아요 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SongLikeResponse {

  @Schema(description = "요청 처리 성공 여부")
  private boolean success;
  
  @Schema(description = "곡 ID")
  private String songId;

}
