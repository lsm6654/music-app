package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.repository.AlbumAnnualAggregationEntityRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumAnnualAggregationRepository extends AlbumAnnualAggregationEntityRepository, R2dbcRepository<AlbumAnnualAggregation, Void> {

}
