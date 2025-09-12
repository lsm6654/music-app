package com.example.music.infrastructure.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumEntity {

  @Id
  @Column("id")
  private Long id;

  @Column("album_name")
  private String albumName;

  @Column("album_type")
  private String albumType;

  @Column("release_date")
  private LocalDate releaseDate;

  @Column("release_year")
  private Short releaseYear;

  @Column("total_songs")
  private Integer totalSongs;

  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column("updated_at")
  private LocalDateTime updatedAt;

}
