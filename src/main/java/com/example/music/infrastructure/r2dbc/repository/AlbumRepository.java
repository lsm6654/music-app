package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.Album;
import com.example.music.domain.repository.AlbumEntityRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends AlbumEntityRepository, R2dbcRepository<Album, Long> {

}
