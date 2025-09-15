package com.example.music.application.scheduler;

import com.example.music.domain.repository.SongLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongLikeAggregationScheduler {

  private final SongLikeRepository songLikeRepository;

  @Scheduled(cron = "0 * * * * *")
  public void aggregateLikes() {
    LocalDateTime now = LocalDateTime.now();
    log.info("Starting like aggregation for time: {}", now);

    songLikeRepository.aggregateLikes(now)
      .doOnSuccess(count -> log.info("Aggregation completed successfully. {} songs aggregated", count))
      .doOnError(error -> log.error("Aggregation failed", error))
      .onErrorResume(error -> {
        log.error("Error during aggregation, will retry next cycle: {}", error.getMessage());
        return Mono.empty();
      })
      .subscribe();
  }
}
