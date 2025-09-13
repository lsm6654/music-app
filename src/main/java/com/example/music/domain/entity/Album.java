package com.example.music.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("albums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"songs", "artists"})
public class Album {

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
  
  @Transient
  @Builder.Default
  private List<Song> songs = new ArrayList<>();
  
  @Transient
  @Builder.Default
  private List<AlbumArtistMapping> artists = new ArrayList<>();

}
