package com.example.music.application.listener;

import com.example.music.common.event.SongLikeEvent;
import com.example.music.domain.entity.EventLog;
import com.example.music.infrastructure.r2dbc.repository.EventLogRepository;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongLikeEventListener {

  private final EventLogRepository eventLogRepository;
  
  @EventListener
  @Async
  public void handleSongLikeEvent(SongLikeEvent event) {
    log.debug("Handling song like event for event: {}", event);
    
    EventLog eventLog = EventLog.builder()
        .ulid(UlidCreator.getUlid().toString())
        .type("LIKE")
        .userId(event.getUserId())
        .songId(event.getSongId())
        .createdAt(LocalDateTime.now())
        .build();
    
    eventLogRepository.save(eventLog)
        .doOnSuccess(saved -> log.debug("Event log saved with ULID: {}", saved.getUlid()))
        .doOnError(error -> log.error("Failed to save event log. cause={}", error.getCause(), error))
        .onErrorResume(error -> {
          log.error("Error saving event log, continuing: {}", error.getMessage());
          return Mono.empty();
        })
        .subscribe();
  }

}
