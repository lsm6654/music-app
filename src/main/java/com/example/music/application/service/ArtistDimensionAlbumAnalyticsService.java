package com.example.music.application.service;

import com.example.music.domain.entity.AlbumAnnualAggregation;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistDimensionAlbumAnalyticsService implements DimensionAlbumAnalyticsService {

  private final AlbumAnnualAggregationRepository albumAnnualAggregationRepository;
  private final ArtistEntityRepository artistRepository;
  
  @Override
  public AnalyticsDimension getSupportedDimension() {
    return AnalyticsDimension.ARTIST_ID;
  }
  
  @Override
  public Mono<AlbumAnnualAnalyticsResponse> analyze(
    AnalyticsDimension dimension, 
    Integer dimensionKey,
    Pageable pageable) {
    
    log.debug("Analyzing album data with ARTIST_ID dimension, key: {}, page: {}, size: {}",
      dimensionKey, pageable.getPageNumber(), pageable.getPageSize());
    
    Long artistId = dimensionKey.longValue();
    
    // 특정 아티스트의 연도별 데이터 조회
    Flux<AlbumAnnualAggregation> aggregations = albumAnnualAggregationRepository
      .findByArtistId(artistId, pageable);

    // 해당 아티스트의 활동 연도 수 조회
    Mono<Long> totalYears = albumAnnualAggregationRepository.countYearsByArtist(artistId);
    
    // 아티스트 정보 조회
    Mono<String> artistNameMono = artistRepository.findById(artistId)
      .map(artist -> artist.getArtistName())
      .defaultIfEmpty("Unknown Artist");
    
    // 연도별 앨범 수 리스트 생성
    Mono<List<AlbumAnalyticsItem>> contentMono = aggregations
      .map(agg -> AlbumAnalyticsItem.builder()
        .year(agg.getReleaseYear())
        .albumCount(agg.getAlbumCount())
        .build())
      .collectList();
    
    // 최종 응답 생성
    return Mono.zip(contentMono, totalYears, artistNameMono)
      .map(tuple -> {
        List<AlbumAnalyticsItem> content = tuple.getT1();
        Long total = tuple.getT2();
        String artistName = tuple.getT3();
        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());
        
        return AlbumAnnualAnalyticsResponse.builder()
          .dimension(dimension)
          .dimensionKey(dimensionKey)
          .dimensionName(artistName)
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
