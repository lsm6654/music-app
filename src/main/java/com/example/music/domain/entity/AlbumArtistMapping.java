package com.example.music.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("album_artist_mappings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"albumId", "artistId"})
@ToString
public class AlbumArtistMapping {

  @Column("album_id")
  private Long albumId;
  
  @Column("artist_id")
  private Long artistId;
  
  @Transient
  private Album album;
  
  @Transient
  private Artist artist;
  
  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;
  
  @LastModifiedDate
  @Column("updated_at")
  private LocalDateTime updatedAt;

}
