package com.example.music.presentation.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Schema(description = "노래 좋아요 request 스키마")
public class SongLikeRequest {

  // FIXME 인증 통해 user_id 받아야 함.
  @NotNull(message = "user_id는 필수입니다")
  @Schema(
    description = "user_id",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  private String userId;

}
