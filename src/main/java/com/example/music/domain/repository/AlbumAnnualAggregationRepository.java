package com.example.music.domain.repository;

import reactor.core.publisher.Mono;

public interface AlbumAnnualAggregationRepository {

  Mono<Void> aggregateAlbumsByYearAndArtist();

  Mono<Long> count();

}
