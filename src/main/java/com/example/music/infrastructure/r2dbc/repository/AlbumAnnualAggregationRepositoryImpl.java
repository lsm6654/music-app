package com.example.music.infrastructure.r2dbc.repository;

import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AlbumAnnualAggregationRepositoryImpl implements AlbumAnnualAggregationRepository {

  private static final String MERGE_ALBUM_AGGREGATION_QUERY = """
    MERGE INTO album_annual_aggregations AS target
    USING (
        SELECT 
            a.release_year,
            aam.artist_id,
            COUNT(DISTINCT a.id) as album_count
        FROM albums a
        INNER JOIN album_artist_mappings aam ON a.id = aam.album_id
        WHERE a.release_year IS NOT NULL
        GROUP BY a.release_year, aam.artist_id
    ) AS source
    ON (target.release_year = source.release_year 
        AND target.artist_id = source.artist_id)
    WHEN MATCHED THEN
        UPDATE SET 
            album_count = source.album_count,
            updated_at = CURRENT_TIMESTAMP
    WHEN NOT MATCHED THEN
        INSERT (release_year, artist_id, album_count, created_at, updated_at)
        VALUES (source.release_year, source.artist_id, source.album_count, 
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    """;

  private static final String COUNT_ALBUM_AGGREGATION_QUERY = "SELECT COUNT(*) FROM album_annual_aggregations";

  private final DatabaseClient databaseClient;

  @Override
  public Mono<Void> aggregateAlbumsByYearAndArtist() {
    return databaseClient.sql(MERGE_ALBUM_AGGREGATION_QUERY)
      .then()
      .doOnSuccess(unused -> log.info("Completed aggregation"))
      .doOnError(error -> log.error("Failed aggregation. cause={}", error.getMessage(), error));
  }

  @Override
  public Mono<Long> count() {
    return databaseClient.sql(COUNT_ALBUM_AGGREGATION_QUERY)
      .map(row -> row.get(0, Long.class))
      .one()
      .defaultIfEmpty(0L);
  }

}
