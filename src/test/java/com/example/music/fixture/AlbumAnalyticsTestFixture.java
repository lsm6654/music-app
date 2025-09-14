package com.example.music.fixture;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.presentation.model.request.AlbumAnnualAnalyticsRequest;
import com.example.music.presentation.model.response.AlbumAnalyticsItem;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import com.example.music.presentation.model.response.PageableResponse;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumAnalyticsTestFixture {

  public static final Long GUCCI_MANE_ID = 608L;
  public static final String GUCCI_MANE_NAME = "Gucci Mane";

  public static final Long LIL_WAYNE_ID = 628L;
  public static final String LIL_WAYNE_NAME = "Lil Wayne";

  public static final Long YOUNG_THUG_ID = 665L;
  public static final String YOUNG_THUG_NAME = "Young Thug";

  public static final Long UNKNOWN_ARTIST_ID = 999L;
  public static final String UNKNOWN_ARTIST_NAME = "Unknown Artist";

  public static final Short YEAR_2024 = 2024;
  public static final Short YEAR_2023 = 2023;
  public static final Short YEAR_2022 = 2022;
  public static final Short YEAR_2020 = 2020;

  public static final Integer GUCCI_2024_ALBUM_COUNT = 17;
  public static final Integer GUCCI_2023_ALBUM_COUNT = 11;
  public static final Integer GUCCI_2022_ALBUM_COUNT = 5;

  public static final Integer WAYNE_2024_ALBUM_COUNT = 15;
  public static final Integer WAYNE_2023_ALBUM_COUNT = 8;

  public static final Integer THUG_2024_ALBUM_COUNT = 11;
  public static final Integer THUG_2022_ALBUM_COUNT = 7;

  public static final Integer DEFAULT_PAGE = 0;
  public static final Integer DEFAULT_SIZE = 20;
  public static final Integer SMALL_SIZE = 2;

  public static final Artist GUCCI_MANE = Artist.builder()
    .artistId(GUCCI_MANE_ID)
    .artistName(GUCCI_MANE_NAME)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  public static final Artist LIL_WAYNE = Artist.builder()
    .artistId(LIL_WAYNE_ID)
    .artistName(LIL_WAYNE_NAME)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  public static final Artist YOUNG_THUG = Artist.builder()
    .artistId(YOUNG_THUG_ID)
    .artistName(YOUNG_THUG_NAME)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  // ===== Aggregation Fixtures =====
  public static final AlbumAnnualAggregation GUCCI_2024_AGGREGATION = AlbumAnnualAggregation.builder()
    .releaseYear(YEAR_2024)
    .artistId(GUCCI_MANE_ID)
    .albumCount(GUCCI_2024_ALBUM_COUNT)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  public static final AlbumAnnualAggregation GUCCI_2023_AGGREGATION = AlbumAnnualAggregation.builder()
    .releaseYear(YEAR_2023)
    .artistId(GUCCI_MANE_ID)
    .albumCount(GUCCI_2023_ALBUM_COUNT)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  public static final AlbumAnnualAggregation WAYNE_2024_AGGREGATION = AlbumAnnualAggregation.builder()
    .releaseYear(YEAR_2024)
    .artistId(LIL_WAYNE_ID)
    .albumCount(WAYNE_2024_ALBUM_COUNT)
    .createdAt(LocalDateTime.now())
    .updatedAt(LocalDateTime.now())
    .build();

  // ===== Request Fixtures =====
  public static final AlbumAnnualAnalyticsRequest YEAR_2024_REQUEST = AlbumAnnualAnalyticsRequest.builder()
    .dimension(AnalyticsDimension.YEAR)
    .dimensionKey(YEAR_2024.intValue())
    .page(DEFAULT_PAGE)
    .size(DEFAULT_SIZE)
    .build();

  public static final AlbumAnnualAnalyticsRequest GUCCI_MANE_REQUEST = AlbumAnnualAnalyticsRequest.builder()
    .dimension(AnalyticsDimension.ARTIST_ID)
    .dimensionKey(GUCCI_MANE_ID.intValue())
    .page(DEFAULT_PAGE)
    .size(DEFAULT_SIZE)
    .build();

  public static final AlbumAnnualAnalyticsResponse YEAR_2024_RESPONSE = AlbumAnnualAnalyticsResponse.builder()
    .dimension(AnalyticsDimension.YEAR)
    .dimensionKey(YEAR_2024.intValue())
    .dimensionName(String.valueOf(YEAR_2024))
    .content(List.of(
      AlbumAnalyticsItem.builder()
        .artistId(GUCCI_MANE_ID)
        .artistName(GUCCI_MANE_NAME)
        .albumCount(GUCCI_2024_ALBUM_COUNT)
        .build(),
      AlbumAnalyticsItem.builder()
        .artistId(LIL_WAYNE_ID)
        .artistName(LIL_WAYNE_NAME)
        .albumCount(WAYNE_2024_ALBUM_COUNT)
        .build()
    ))
    .pageable(PageableResponse.builder()
      .pageNumber(DEFAULT_PAGE)
      .pageSize(DEFAULT_SIZE)
      .totalElements(2L)
      .totalPages(1)
      .build())
    .first(true)
    .last(true)
    .build();

  public static final AlbumAnnualAnalyticsResponse GUCCI_MANE_RESPONSE = AlbumAnnualAnalyticsResponse.builder()
    .dimension(AnalyticsDimension.ARTIST_ID)
    .dimensionKey(GUCCI_MANE_ID.intValue())
    .dimensionName(GUCCI_MANE_NAME)
    .content(List.of(
      AlbumAnalyticsItem.builder()
        .year(YEAR_2024)
        .albumCount(GUCCI_2024_ALBUM_COUNT)
        .build(),
      AlbumAnalyticsItem.builder()
        .year(YEAR_2023)
        .albumCount(GUCCI_2023_ALBUM_COUNT)
        .build(),
      AlbumAnalyticsItem.builder()
        .year(YEAR_2022)
        .albumCount(GUCCI_2022_ALBUM_COUNT)
        .build()
    ))
    .pageable(PageableResponse.builder()
      .pageNumber(DEFAULT_PAGE)
      .pageSize(DEFAULT_SIZE)
      .totalElements(3L)
      .totalPages(1)
      .build())
    .first(true)
    .last(true)
    .build();

  private AlbumAnalyticsTestFixture() {
    throw new UnsupportedOperationException("This is a fixture class and cannot be instantiated");
  }

}
