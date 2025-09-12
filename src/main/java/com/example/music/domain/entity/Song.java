package com.example.music.domain.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "album")
public class Song {

  private Long id;
  private Album album;
  private String songTitle;
  private BigDecimal danceability;
  private BigDecimal energy;
  private BigDecimal positiveness;
  private BigDecimal liveness;
  private BigDecimal instrumentalness;
  private Byte popularity;

}
