package com.example.music.domain.repository;

import com.example.music.domain.entity.Song;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface SongEntityRepository extends ReactiveCrudRepository<Song, Long> {

}
