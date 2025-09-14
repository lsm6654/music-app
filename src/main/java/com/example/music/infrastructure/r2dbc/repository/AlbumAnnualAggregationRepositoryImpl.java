package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AlbumAnnualAggregationRepositoryImpl implements AlbumAnnualAggregationRepository {

  private final DatabaseClient databaseClient;

  @Override
  public Mono<Void> aggregateAlbumsByYearAndArtist() {
    return databaseClient.sql(AlbumAnnualAggregationQueries.MERGE_ALBUM_AGGREGATION)
      .then()
      .doOnSuccess(unused -> log.info("Completed aggregation"))
      .doOnError(error -> log.error("Failed aggregation. cause={}", error.getMessage(), error));
  }

  @Override
  public Mono<Long> count() {
    return databaseClient.sql(AlbumAnnualAggregationQueries.COUNT_ALL)
      .map(row -> row.get(0, Long.class))
      .one()
      .defaultIfEmpty(0L);
  }
  
  @Override
  public Flux<AlbumAnnualAggregation> findByReleaseYear(Short year, Pageable pageable) {
    return databaseClient.sql(AlbumAnnualAggregationQueries.FIND_BY_RELEASE_YEAR)
      .bind("year", year)
      .bind("limit", pageable.getPageSize())
      .bind("offset", pageable.getOffset())
      .map(this::mapToAlbumAnnualAggregation)
      .all();
  }
  
  @Override
  public Flux<AlbumAnnualAggregation> findByArtistId(Long artistId, Pageable pageable) {
    return databaseClient.sql(AlbumAnnualAggregationQueries.FIND_BY_ARTIST_ID_PAGED)
      .bind("artistId", artistId)
      .bind("limit", pageable.getPageSize())
      .bind("offset", pageable.getOffset())
      .map(this::mapToAlbumAnnualAggregation)
      .all();
  }
  
  @Override
  public Mono<Long> countArtistsByYear(Short year) {
    return databaseClient.sql(AlbumAnnualAggregationQueries.COUNT_ARTISTS_BY_YEAR)
      .bind("year", year)
      .map(row -> row.get(0, Long.class))
      .one()
      .defaultIfEmpty(0L);
  }
  
  @Override
  public Mono<Long> countYearsByArtist(Long artistId) {
    return databaseClient.sql(AlbumAnnualAggregationQueries.COUNT_YEARS_BY_ARTIST)
      .bind("artistId", artistId)
      .map(row -> row.get(0, Long.class))
      .one()
      .defaultIfEmpty(0L);
  }
  
  /**
   * Row를 AlbumAnnualAggregation 엔티티로 매핑
   */
  private AlbumAnnualAggregation mapToAlbumAnnualAggregation(
      io.r2dbc.spi.Row row, 
      io.r2dbc.spi.RowMetadata metadata) {
    return AlbumAnnualAggregation.builder()
      .releaseYear(row.get("release_year", Short.class))
      .artistId(row.get("artist_id", Long.class))
      .albumCount(row.get("album_count", Integer.class))
      .build();
  }
  
  /**
   * Row를 AlbumAnnualAggregation 엔티티로 매핑 (총 앨범 수 포함)
   */
  private AlbumAnnualAggregation mapToAlbumAnnualAggregationWithTotal(
      io.r2dbc.spi.Row row,
      io.r2dbc.spi.RowMetadata metadata) {
    Long artistId = row.get("artist_id", Long.class);
    // H2 데이터베이스에서 SUM 결과는 Long으로 반환될 수 있음
    Number totalAlbums = (Number) row.get("total_albums");
    Integer albumCount = totalAlbums != null ? totalAlbums.intValue() : 0;
    
    return AlbumAnnualAggregation.builder()
      .artistId(artistId)
      .albumCount(albumCount)
      .build();
  }

}
