package com.example.music.infrastructure.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("album_artist_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumArtistMappingEntity {

  @Column("album_id")
  private Long albumId;

  @Column("artist_id")
  private Long artistId;

  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column("updated_at")
  private LocalDateTime updatedAt;

}
