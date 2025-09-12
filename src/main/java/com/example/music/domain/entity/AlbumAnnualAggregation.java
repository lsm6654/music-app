package com.example.music.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AlbumAnnualAggregation {

  private Short releaseYear;
  private Long artistId;
  private Integer albumCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

}
