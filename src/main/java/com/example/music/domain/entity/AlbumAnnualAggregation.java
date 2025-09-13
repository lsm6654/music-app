package com.example.music.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("album_annual_aggregations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AlbumAnnualAggregation {

  @Column("release_year")
  private Short releaseYear;
  
  @Column("artist_id")
  private Long artistId;
  
  @Column("album_count")
  private Integer albumCount;
  
  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;
  
  @LastModifiedDate
  @Column("updated_at")
  private LocalDateTime updatedAt;

}
