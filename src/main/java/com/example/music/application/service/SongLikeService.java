package com.example.music.application.service;

import com.example.music.common.event.SongLikeEvent;
import com.example.music.domain.repository.SongLikeRepository;
import com.example.music.presentation.model.TopTrendResponseFactory;
import com.example.music.presentation.model.response.TopTrendResponse;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongLikeService {

  private final SongLikeRepository songLikeRepository;
  private final ApplicationEventPublisher eventPublisher;

  // throttle 처리를 위한 Caffeine 캐시
  private final Cache<String, AtomicLong> likeCountBuffer = Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterAccess(60, TimeUnit.MINUTES)  // 1시간 액세스 없으면 자동 제거
    .build();

  public Mono<Void> addLike(String songId, String userId) {
    // 메모리 버퍼에 좋아요 카운트 증가
    likeCountBuffer.asMap()
      .computeIfAbsent(songId, k -> new AtomicLong(0))
      .incrementAndGet();

    // 이벤트 발행
    SongLikeEvent event = SongLikeEvent.of(songId, userId);
    eventPublisher.publishEvent(event);

    return Mono.empty();
  }

  /**
   * 2초마다 버퍼에 쌓인 좋아요 수를 Redis에 반영
   * Caffeine 캐시가 자동으로 만료된 항목을 제거하므로 별도 정리 불필요
   * 에러 발생한 경우, 롤백 필요하지 않으며 자체적으로 재처리됨
   */
  @Scheduled(fixedDelay = 2000)
  public void flushLikeBuffer() {
    ConcurrentMap<String, AtomicLong> cacheMap = likeCountBuffer.asMap();
    if (cacheMap.isEmpty()) {
      return;
    }

    // flush 집계 시작 시점의 시간을 기준으로 increment 처리.
    LocalDateTime now = LocalDateTime.now();

    // 버퍼의 모든 항목을 처리
    cacheMap.forEach((songId, count) -> {
      try {
        // 현재 카운트 값 읽음
        long likeCount = count.get();

        // increment 할 카운트가 없다면, skip
        if (likeCount < 0) {
          return;
        }

        // 한번에 increment 처리
        songLikeRepository.incrementLikeBy(songId, now, likeCount)
          .subscribe(
            score -> {
              // 성공한 경우, 카운트에서 차감
              count.addAndGet(-likeCount);
              log.debug("Flushed {} likes for songId: {}, new score: {}", likeCount, songId, score);
            },
            error -> {
              // 실패한 경우, 카운트를 리셋하지 않았으므로 다음 주기에 자동으로 재시도
            }
          );
      } catch (Exception e) {
        // 실패한 경우, 카운트를 리셋하지 않았으므로 다음 주기에 자동으로 재시도
      }
    });
  }

  @Cacheable(value = "songTrends", key = "'trends'")
  public Mono<TopTrendResponse> getTopTrends() {
    return songLikeRepository.getTopTrends(10)
      .collectList()
      .map(TopTrendResponseFactory::createFrom);
  }

}
