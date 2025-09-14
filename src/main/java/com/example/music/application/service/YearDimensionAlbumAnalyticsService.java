package com.example.music.application.service;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import com.example.music.domain.repository.ArtistEntityRepository;
import com.example.music.presentation.model.response.AlbumAnalyticsItem;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import com.example.music.presentation.model.response.PageableResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class YearDimensionAlbumAnalyticsService implements DimensionAlbumAnalyticsService {

  private final AlbumAnnualAggregationRepository albumAnnualAggregationRepository;
  private final ArtistEntityRepository artistRepository;

  @Override
  public AnalyticsDimension getSupportedDimension() {
    return AnalyticsDimension.YEAR;
  }

  @Override
  public Mono<AlbumAnnualAnalyticsResponse> analyze(
    AnalyticsDimension dimension,
    Integer dimensionKey,
    Pageable pageable) {

    log.debug("Analyzing album data with YEAR dimension, key: {}, page: {}, size: {}",
      dimensionKey, pageable.getPageNumber(), pageable.getPageSize());

    Short year = dimensionKey.shortValue();
    
    // 특정 연도의 아티스트별 데이터 조회
    Flux<AlbumAnnualAggregation> aggregations = albumAnnualAggregationRepository
      .findByReleaseYear(year, pageable);

    // 해당 연도의 총 아티스트 수 조회
    Mono<Long> totalArtists = albumAnnualAggregationRepository.countArtistsByYear(year);

    // 아티스트 정보 조회를 위한 맵 생성
    Mono<List<AlbumAnalyticsItem>> contentMono = aggregations
      .flatMap(agg -> 
        artistRepository.findById(agg.getArtistId())
          .map(artist -> AlbumAnalyticsItem.builder()
            .artistId(artist.getArtistId())
            .artistName(artist.getArtistName())
            .albumCount(agg.getAlbumCount())
            .build())
          .defaultIfEmpty(AlbumAnalyticsItem.builder()
            .artistId(agg.getArtistId())
            .artistName("Unknown Artist")
            .albumCount(agg.getAlbumCount())
            .build())
      )
      .collectList();
    
    // 최종 응답 생성
    return Mono.zip(contentMono, totalArtists)
      .map(tuple -> {
        List<AlbumAnalyticsItem> content = tuple.getT1();
        Long total = tuple.getT2();
        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        return AlbumAnnualAnalyticsResponse.builder()
          .dimension(dimension)
          .dimensionKey(dimensionKey)
          .dimensionName(String.valueOf(year))
          .content(content)
          .pageable(PageableResponse.builder()
            .pageNumber(pageable.getPageNumber())
            .pageSize(pageable.getPageSize())
            .totalElements(total)
            .totalPages(totalPages)
            .build())
          .first(pageable.getPageNumber() == 0)
          .last(pageable.getPageNumber() >= totalPages - 1)
          .build();
      });
  }

}
