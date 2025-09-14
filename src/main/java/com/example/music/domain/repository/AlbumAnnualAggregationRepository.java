package com.example.music.domain.repository;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumAnnualAggregationRepository {

  Mono<Void> aggregateAlbumsByYearAndArtist();

  Mono<Long> count();
  
  // 특정 연도의 아티스트별 앨범 수 조회
  Flux<AlbumAnnualAggregation> findByReleaseYear(Short year, Pageable pageable);
  
  // 특정 아티스트의 연도별 앨범 수 조회
  Flux<AlbumAnnualAggregation> findByArtistId(Long artistId, Pageable pageable);
  
  // 특정 연도의 총 아티스트 수
  Mono<Long> countArtistsByYear(Short year);
  
  // 특정 아티스트의 활동 연도 수
  Mono<Long> countYearsByArtist(Long artistId);

}
