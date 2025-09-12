package com.example.music.infrastructure.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@Table("album_annual_aggregations")
public class AlbumAnnualAggregationEntity {

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
