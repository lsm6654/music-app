package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.AlbumArtistMapping;
import com.example.music.domain.repository.AlbumArtistMappingEntityRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumArtistMappingRepository extends AlbumArtistMappingEntityRepository, R2dbcRepository<AlbumArtistMapping, Void> {

}
