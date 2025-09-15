package com.example.music.infrastructure.redis.repository;

import com.example.music.domain.repository.SongLikeRepository;
import com.example.music.infrastructure.redis.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SongLikeRedisRepository implements SongLikeRepository {

  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
  private final RedisKeyGenerator keyGenerator;

  @Override
  public Mono<Double> incrementLike(String songId, LocalDateTime dateTime) {
    String key = keyGenerator.generateMinuteKey(dateTime);

    return reactiveRedisTemplate.opsForZSet()
      .incrementScore(key, songId, 1)
      .flatMap(score -> reactiveRedisTemplate.expire(key, Duration.ofDays(7)).thenReturn(score));
  }

  @Override
  public Mono<Double> incrementLikeBy(String songId, LocalDateTime dateTime, long increment) {
    String key = keyGenerator.generateMinuteKey(dateTime);

    return reactiveRedisTemplate.opsForZSet()
      .incrementScore(key, songId, increment)
      .flatMap(score -> reactiveRedisTemplate.expire(key, Duration.ofDays(7)).thenReturn(score));
  }

  @Override
  public Flux<TypedTuple<String>> getTopTrends(int limit) {
    LocalDateTime now = LocalDateTime.now();
    String trendsKey = keyGenerator.generateTrendsKey(now);

    return reactiveRedisTemplate.opsForZSet()
      .reverseRangeWithScores(trendsKey, Range.closed(0L, (long) (limit - 1)));
  }

  @Override
  public Mono<Long> aggregateLikes(LocalDateTime targetTime) {
    String targetKey = keyGenerator.generateTrendsKey(targetTime);
    List<String> sourceKeys = generateSourceKeys(targetTime);

    if (sourceKeys.isEmpty() || sourceKeys.size() == 1) {
      log.error("Failed to extract source keys for aggregation. sourceKeys: {}", sourceKeys);
      return Mono.just(0L);
    }

    return reactiveRedisTemplate.opsForZSet()
      .unionAndStore(sourceKeys.getFirst(), sourceKeys.subList(1, sourceKeys.size()), targetKey)
      .flatMap(count -> {
        // 최대 1000개 members 관리 및 TTL 7d
        return reactiveRedisTemplate.opsForZSet()
          .removeRange(targetKey, Range.closed(0L, -1001L))
          .then(reactiveRedisTemplate.expire(targetKey, Duration.ofDays(7)))
          .thenReturn(count);
      })
      .doOnSuccess(count -> log.info("Aggregation completed. {} unique songs in trends", count))
      .doOnError(error -> log.error("Aggregation failed", error));
  }

  // 해당 시간으로부터 최근 60분의 모든 source keys
  private List<String> generateSourceKeys(LocalDateTime targetTime) {
    return IntStream.range(0, 60)
      .mapToObj(i -> {
        LocalDateTime time = targetTime.minusMinutes(i);
        return keyGenerator.generateMinuteKey(time);
      })
      .collect(Collectors.toList());
  }

}
