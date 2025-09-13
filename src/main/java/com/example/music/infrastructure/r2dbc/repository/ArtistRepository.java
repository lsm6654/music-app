package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.Artist;
import com.example.music.domain.repository.ArtistEntityRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends ArtistEntityRepository, R2dbcRepository<Artist, Long> {

}
