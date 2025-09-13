package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.Song;
import com.example.music.domain.repository.SongEntityRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends SongEntityRepository, R2dbcRepository<Song, Long> {

}
