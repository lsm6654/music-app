package com.example.music.domain.repository;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@NoRepositoryBean
public interface SongLikeRepository {

  Mono<Double> incrementLike(String songId, LocalDateTime dateTime);

  Mono<Double> incrementLikeBy(String songId, LocalDateTime dateTime, long increment);
  
  Mono<Long> aggregateLikes(LocalDateTime targetTime);

  Flux<TypedTuple<String>> getTopTrends(int limit);

}
