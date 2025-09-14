package com.example.music.application.service;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import com.example.music.domain.repository.ArtistEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static com.example.music.fixture.AlbumAnalyticsTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistDimensionAlbumAnalyticsServiceTest {

  @Mock
  private AlbumAnnualAggregationRepository aggregationRepository;

  @Mock
  private ArtistEntityRepository artistRepository;

  @InjectMocks
  private ArtistDimensionAlbumAnalyticsService service;

  private Integer dimensionKey;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    dimensionKey = 608;
    pageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("특정 아티스트의 연도별 데이터 조회 - 정상 케이스")
  void analyze_WithArtistDimension_ShouldReturnYearlyData() {
    // Given - Fixture 사용
    Long artistId = GUCCI_MANE_ID;

    when(aggregationRepository.findByArtistId(eq(artistId), eq(pageable)))
      .thenReturn(Flux.just(GUCCI_2024_AGGREGATION, GUCCI_2023_AGGREGATION, 
        AlbumAnnualAggregation.builder()
          .releaseYear(YEAR_2022)
          .artistId(GUCCI_MANE_ID)
          .albumCount(GUCCI_2022_ALBUM_COUNT)
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build()));
    when(aggregationRepository.countYearsByArtist(eq(artistId)))
      .thenReturn(Mono.just(3L));
    when(artistRepository.findById(artistId))
      .thenReturn(Mono.just(GUCCI_MANE));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.ARTIST_ID, dimensionKey, pageable))
      .assertNext(response -> {
        assertThat(response).isNotNull();
        assertThat(response.getDimension()).isEqualTo(AnalyticsDimension.ARTIST_ID);
        assertThat(response.getDimensionKey()).isEqualTo(608);
        assertThat(response.getDimensionName()).isEqualTo(GUCCI_MANE_NAME);
        assertThat(response.getContent()).hasSize(3);
        
        assertThat(response.getContent().get(0).getYear()).isEqualTo(YEAR_2024);
        assertThat(response.getContent().get(0).getAlbumCount()).isEqualTo(GUCCI_2024_ALBUM_COUNT);
        
        assertThat(response.getContent().get(1).getYear()).isEqualTo(YEAR_2023);
        assertThat(response.getContent().get(1).getAlbumCount()).isEqualTo(GUCCI_2023_ALBUM_COUNT);
        
        assertThat(response.getContent().get(2).getYear()).isEqualTo(YEAR_2022);
        assertThat(response.getContent().get(2).getAlbumCount()).isEqualTo(GUCCI_2022_ALBUM_COUNT);
        
        assertThat(response.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(response.getPageable().getPageSize()).isEqualTo(10);
        assertThat(response.getPageable().getTotalElements()).isEqualTo(3L);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("존재하지 않는 아티스트 조회 - Unknown Artist 반환")
  void analyze_WithUnknownArtist_ShouldReturnUnknownArtist() {
    // Given
    Long unknownArtistId = 999L;
    Integer unknownDimensionKey = 999;

    when(aggregationRepository.findByArtistId(eq(unknownArtistId), eq(pageable)))
      .thenReturn(Flux.empty());
    when(aggregationRepository.countYearsByArtist(eq(unknownArtistId)))
      .thenReturn(Mono.just(0L));
    when(artistRepository.findById(unknownArtistId))
      .thenReturn(Mono.empty());

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.ARTIST_ID, unknownDimensionKey, pageable))
      .assertNext(response -> {
        assertThat(response.getDimensionKey()).isEqualTo(999);
        assertThat(response.getDimensionName()).isEqualTo("Unknown Artist");
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getPageable().getTotalElements()).isEqualTo(0L);
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("아티스트는 존재하지만 데이터가 없는 경우")
  void analyze_WithNoAggregationData_ShouldReturnEmptyContent() {
    // Given
    Long artistId = GUCCI_MANE_ID;

    when(aggregationRepository.findByArtistId(eq(artistId), eq(pageable)))
      .thenReturn(Flux.empty());
    when(aggregationRepository.countYearsByArtist(eq(artistId)))
      .thenReturn(Mono.just(0L));
    when(artistRepository.findById(artistId))
      .thenReturn(Mono.just(GUCCI_MANE));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.ARTIST_ID, dimensionKey, pageable))
      .assertNext(response -> {
        assertThat(response.getDimensionName()).isEqualTo(GUCCI_MANE_NAME);
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getPageable().getTotalElements()).isEqualTo(0L);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("페이징 처리 확인")
  void analyze_WithPaging_ShouldReturnPagedResult() {
    // Given
    Long artistId = GUCCI_MANE_ID;
    Pageable secondPage = PageRequest.of(1, 2);
    AlbumAnnualAggregation agg = AlbumAnnualAggregation.builder()
      .releaseYear(YEAR_2020)
      .artistId(artistId)
      .albumCount(9)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    when(aggregationRepository.findByArtistId(eq(artistId), eq(secondPage)))
      .thenReturn(Flux.just(agg));
    when(aggregationRepository.countYearsByArtist(eq(artistId)))
      .thenReturn(Mono.just(10L)); // 총 10개 연도 데이터
    when(artistRepository.findById(artistId))
      .thenReturn(Mono.just(GUCCI_MANE));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.ARTIST_ID, dimensionKey, secondPage))
      .assertNext(response -> {
        assertThat(response.getPageable().getPageNumber()).isEqualTo(1);
        assertThat(response.getPageable().getPageSize()).isEqualTo(2);
        assertThat(response.getPageable().getTotalElements()).isEqualTo(10L);
        assertThat(response.getPageable().getTotalPages()).isEqualTo(5); // 10개 데이터, 페이지당 2개
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
      })
      .verifyComplete();
  }

}
