package com.example.music.domain.repository;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface AlbumAnnualAggregationEntityRepository extends ReactiveCrudRepository<AlbumAnnualAggregation, Void> {

}
