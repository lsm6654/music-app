package com.example.music.domain.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "artistId")
@ToString(exclude = "albums")
public class Artist {

  private Long artistId;
  private String artistName;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  
  @Builder.Default
  private List<Album> albums = new ArrayList<>();

}
