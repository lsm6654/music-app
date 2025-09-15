package com.example.music.application.service;

import com.example.music.domain.entity.Album;
import com.example.music.domain.entity.AlbumArtistMapping;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.entity.Song;
import com.example.music.domain.factory.MusicEntityFactory;
import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import com.example.music.infrastructure.r2dbc.repository.AlbumArtistMappingRepository;
import com.example.music.infrastructure.r2dbc.repository.AlbumRepository;
import com.example.music.infrastructure.r2dbc.repository.ArtistRepository;
import com.example.music.infrastructure.r2dbc.repository.SongRepository;
import com.example.music.util.MusicJsonDataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService {

  private final MusicJsonDataLoader musicJsonDataLoader;
  private final MusicEntityFactory entityFactory;
  private final ArtistRepository artistRepository;
  private final AlbumRepository albumRepository;
  private final SongRepository songRepository;
  private final AlbumArtistMappingRepository albumArtistMappingRepository;
  private final AlbumAnnualAggregationRepository albumAnnualAggregationRepository;

  // ID 매핑위한 Map
  private final Map<String, Long> artistIdMap = new ConcurrentHashMap<>();
  private final Map<String, Long> albumIdMap = new ConcurrentHashMap<>();

  private static final String DATA_FILE_PATH = "classpath:init-data/dataset.json";
  private static final int BATCH_SIZE = 1000;
  private static final int CONCURRENT_SAVES = 10;

  public void loadBaseData() {
    try {
      initializeBaseData()
        .doOnTerminate(() -> log.info("Data initialization completed."))
        .doOnError(error -> log.error("Data initialization failed.", error))
        .block();  // 초기화 완료 대기
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize data.", e);
    }
  }

  public void loadAggregationData() {
    try {
      aggregateAlbumData()
        .doOnTerminate(() -> log.info("Aggregation data initialization completed."))
        .doOnError(error -> log.error("Aggregation data initialization failed.", error))
        .block();  // 집계 완료 대기

    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize aggregation data.", e);
    }
  }

  private Mono<Void> initializeBaseData() {
    return songRepository.count()
      .map(count -> count == 0)
      .defaultIfEmpty(true)
      .flatMap(isEmpty -> {
        if (!isEmpty) {
          log.info("Database already contains data. Skipping initialization.");
          return Mono.empty();
        }
        return musicJsonDataLoader.loadDataInBatches(DATA_FILE_PATH, BATCH_SIZE)
          .flatMap(this::processBatch, CONCURRENT_SAVES)
          .then();
      });
  }

  private Mono<Void> processBatch(MusicJsonDataLoader.MusicData batch) {
    return Flux.concat(
      saveArtists(batch),
      saveAlbums(batch),
      saveSongs(batch),
      saveAlbumArtistMappings(batch)
    ).then();
  }

  private Mono<Void> saveArtists(MusicJsonDataLoader.MusicData batch) {
    return Flux.fromIterable(batch.getArtists())
      .flatMap(artistData -> {
        String key = artistData.getArtistName();
        if (artistIdMap.containsKey(key)) {
          return Mono.empty();
        }

        Artist entity = entityFactory.createArtist(artistData);

        return artistRepository.save(entity)
          .doOnSuccess(saved -> artistIdMap.put(key, saved.getArtistId()));
      }, CONCURRENT_SAVES)
      .then();
  }

  private Mono<Void> saveAlbums(MusicJsonDataLoader.MusicData batch) {
    return Flux.fromIterable(batch.getAlbums())
      .flatMap(albumData -> {
        String key = albumData.getAlbumName();
        if (albumIdMap.containsKey(key)) {
          return Mono.empty();
        }

        Album entity = entityFactory.createAlbum(albumData);

        return albumRepository.save(entity)
          .doOnSuccess(saved -> albumIdMap.put(key, saved.getId()));
      }, CONCURRENT_SAVES)
      .then();
  }

  private Mono<Void> saveSongs(MusicJsonDataLoader.MusicData batch) {
    return Flux.fromIterable(batch.getSongs())
      .flatMap(songData -> {
        Long albumId = albumIdMap.get(songData.getAlbum().getAlbumName());
        if (albumId == null) {
          return Mono.empty();
        }

        Song entity = entityFactory.createSong(songData, albumId);

        return songRepository.save(entity);
      }, CONCURRENT_SAVES)
      .then();
  }

  private Mono<Void> saveAlbumArtistMappings(MusicJsonDataLoader.MusicData batch) {
    return Flux.fromIterable(batch.getAlbumArtistMappings())
      .flatMap(mappingData -> {
        Long albumId = albumIdMap.get(mappingData.getAlbum().getAlbumName());
        Long artistId = artistIdMap.get(mappingData.getArtist().getArtistName());

        if (albumId == null || artistId == null) {
          return Mono.empty();
        }

        AlbumArtistMapping entity = entityFactory.createAlbumArtistMapping(mappingData, albumId, artistId);

        return albumArtistMappingRepository.save(entity)
          .onErrorResume(e -> {
            // 중복 키 에러인 경우만 무시
            if (e instanceof DataIntegrityViolationException) {
              return Mono.empty();
            }
            // 다른 에러는 throw
            return Mono.error(e);
          });
      }, CONCURRENT_SAVES)
      .then();
  }

  private Mono<Void> aggregateAlbumData() {
    return albumAnnualAggregationRepository.count()
      .flatMap(count -> {
        if (count > 0) {
          log.info("Album annual aggregation data already exists. Skipping aggregation.");
          return Mono.empty();
        }
        return albumAnnualAggregationRepository.aggregateAlbumsByYearAndArtist()
          .doOnSuccess(unused -> log.info("Album annual aggregation completed"))
          .doOnError(error -> log.error("Failed to aggregate album data", error));
      });
  }

}
