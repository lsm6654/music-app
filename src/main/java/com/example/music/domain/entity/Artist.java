package com.example.music.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table("artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "artistId")
@ToString(exclude = "albums")
public class Artist {

  @Id
  @Column("artist_id")
  private Long artistId;
  
  @Column("artist_name")
  private String artistName;
  
  @CreatedDate
  @Column("created_at")
  private LocalDateTime createdAt;
  
  @LastModifiedDate
  @Column("updated_at")
  private LocalDateTime updatedAt;
  
  @Transient
  @Builder.Default
  private List<Album> albums = new ArrayList<>();

}
