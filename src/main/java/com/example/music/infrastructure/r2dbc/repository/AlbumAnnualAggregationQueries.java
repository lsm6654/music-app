package com.example.music.infrastructure.r2dbc.repository;

public final class AlbumAnnualAggregationQueries {

  private AlbumAnnualAggregationQueries() {
  }
  
  /**
   * 앨범 집계 데이터 MERGE 쿼리
   * - 연도별, 아티스트별 앨범 수를 집계하여 저장
   * - 기존 데이터가 있으면 UPDATE, 없으면 INSERT
   */
  public static final String MERGE_ALBUM_AGGREGATION = """
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
  
  /**
   * 전체 집계 데이터 개수 조회
   */
  public static final String COUNT_ALL = 
    "SELECT COUNT(*) FROM album_annual_aggregations";
  
  /**
   * 연도별 집계 데이터 조회 (페이징)
   * - 연도 내림차순, 앨범 수 내림차순 정렬
   */
  public static final String FIND_BY_YEAR = """
    SELECT aaa.release_year, aaa.artist_id, aaa.album_count, 
           aaa.created_at, aaa.updated_at
    FROM album_annual_aggregations aaa
    ORDER BY aaa.release_year DESC, aaa.album_count DESC
    LIMIT :limit OFFSET :offset
    """;
  
  /**
   * 고유 연도 수 조회
   */
  public static final String COUNT_DISTINCT_YEARS = 
    "SELECT COUNT(DISTINCT release_year) FROM album_annual_aggregations";
  
  /**
   * 특정 아티스트의 연도별 앨범 수 조회
   */
  public static final String FIND_BY_ARTIST_ID = """
    SELECT aaa.release_year, aaa.artist_id, aaa.album_count,
           aaa.created_at, aaa.updated_at
    FROM album_annual_aggregations aaa
    WHERE aaa.artist_id = :artistId
    ORDER BY aaa.release_year DESC
    """;
  
  /**
   * 아티스트별 총 앨범 수 집계 (페이징)
   * - 총 앨범 수 내림차순 정렬
   */
  public static final String FIND_GROUP_BY_ARTIST = """
    SELECT aaa.artist_id, SUM(aaa.album_count) as total_albums
    FROM album_annual_aggregations aaa
    GROUP BY aaa.artist_id
    ORDER BY total_albums DESC
    LIMIT :limit OFFSET :offset
    """;
  
  /**
   * 고유 아티스트 수 조회
   */
  public static final String COUNT_DISTINCT_ARTISTS = 
    "SELECT COUNT(DISTINCT artist_id) FROM album_annual_aggregations";
  
  /**
   * 특정 연도의 아티스트별 앨범 수 조회
   */
  public static final String FIND_BY_RELEASE_YEAR = """
    SELECT aaa.release_year, aaa.artist_id, aaa.album_count,
           aaa.created_at, aaa.updated_at
    FROM album_annual_aggregations aaa
    WHERE aaa.release_year = :year
    ORDER BY aaa.album_count DESC
    LIMIT :limit OFFSET :offset
    """;
  
  /**
   * 특정 아티스트의 연도별 앨범 수 조회 (페이징)
   */
  public static final String FIND_BY_ARTIST_ID_PAGED = """
    SELECT aaa.release_year, aaa.artist_id, aaa.album_count,
           aaa.created_at, aaa.updated_at
    FROM album_annual_aggregations aaa
    WHERE aaa.artist_id = :artistId
    ORDER BY aaa.release_year DESC
    LIMIT :limit OFFSET :offset
    """;
  
  /**
   * 특정 연도의 총 아티스트 수 조회
   */
  public static final String COUNT_ARTISTS_BY_YEAR = """
    SELECT COUNT(DISTINCT artist_id)
    FROM album_annual_aggregations
    WHERE release_year = :year
    """;

  /**
   * 특정 아티스트의 활동 연도 수 조회
   */
  public static final String COUNT_YEARS_BY_ARTIST = """
    SELECT COUNT(DISTINCT release_year)
    FROM album_annual_aggregations
    WHERE artist_id = :artistId
    """;

}
