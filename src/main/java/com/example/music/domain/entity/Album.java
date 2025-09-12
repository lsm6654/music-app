package com.example.music.domain.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"songs", "artists"})
public class Album {

  private Long id;
  private String albumName;
  private LocalDate releaseDate;
  private Short releaseYear;
  private Integer totalSongs;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  
  @Builder.Default
  private List<Song> songs = new ArrayList<>();
  
  @Builder.Default
  private List<AlbumArtistMapping> artists = new ArrayList<>();

}
