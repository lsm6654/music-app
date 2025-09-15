package com.example.music.common.event;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SongLikeEvent {

  private String songId;
  private String userId;
  private LocalDateTime occurredAt;

  public static SongLikeEvent of(String songId, String userId) {
    return SongLikeEvent.builder()
      .songId(songId)
      .userId(userId)
      .occurredAt(LocalDateTime.now())
      .build();
  }

}
