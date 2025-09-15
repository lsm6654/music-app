package com.example.music.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table("event_logs")
public class EventLog {

  @Id
  @Column("ulid")
  private String ulid;

  @Column("type")
  private String type;

  @Column("user_id")
  private String userId;

  @Column("song_id")
  private String songId;

  @Column("created_at")
  private LocalDateTime createdAt;

  @Column("deleted_at")
  private LocalDateTime deletedAt;

}
