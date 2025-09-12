package com.example.music.infrastructure.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongEntity {

  @Id
  @Column("id")
  private Long id;

  @Column("album_id")
  private Long albumId;

  @Column("song_title")
  private String songTitle;

  @Column("danceability")
  private BigDecimal danceability;

  @Column("energy")
  private BigDecimal energy;

  @Column("positiveness")
  private BigDecimal positiveness;

  @Column("liveness")
  private BigDecimal liveness;

  @Column("instrumentalness")
  private BigDecimal instrumentalness;

  @Column("popularity")
  private Byte popularity;

}
