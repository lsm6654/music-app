package com.example.music.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"album", "artist"})
@ToString
public class AlbumArtistMapping {

  private Album album;
  private Artist artist;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
