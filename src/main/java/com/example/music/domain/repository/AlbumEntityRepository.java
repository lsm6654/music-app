package com.example.music.domain.repository;

import com.example.music.domain.entity.Album;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface AlbumEntityRepository extends ReactiveCrudRepository<Album, Long> {

}
