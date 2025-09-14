package com.example.music.application.service;

import com.example.music.domain.entity.AlbumAnnualAggregation;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.domain.repository.AlbumAnnualAggregationRepository;
import com.example.music.domain.repository.ArtistEntityRepository;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class YearDimensionAlbumAnalyticsServiceTest {

  @Mock
  private AlbumAnnualAggregationRepository aggregationRepository;

  @Mock
  private ArtistEntityRepository artistRepository;

  @InjectMocks
  private YearDimensionAlbumAnalyticsService service;

  private Integer dimensionKey;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    dimensionKey = 2024;
    pageable = PageRequest.of(0, 10);
  }

  @Test
  @DisplayName("특정 연도의 아티스트별 데이터 조회 - 정상 케이스")
  void analyze_WithYearDimension_ShouldReturnArtistData() {
    // Given - Fixture 사용
    when(aggregationRepository.findByReleaseYear(eq(YEAR_2024), eq(pageable)))
      .thenReturn(Flux.just(GUCCI_2024_AGGREGATION, WAYNE_2024_AGGREGATION));
    when(aggregationRepository.countArtistsByYear(eq(YEAR_2024)))
      .thenReturn(Mono.just(2L));
    when(artistRepository.findById(GUCCI_MANE_ID))
      .thenReturn(Mono.just(GUCCI_MANE));
    when(artistRepository.findById(LIL_WAYNE_ID))
      .thenReturn(Mono.just(LIL_WAYNE));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.YEAR, dimensionKey, pageable))
      .assertNext(response -> {
        assertThat(response).isNotNull();
        assertThat(response.getDimension()).isEqualTo(AnalyticsDimension.YEAR);
        assertThat(response.getDimensionKey()).isEqualTo(2024);
        assertThat(response.getDimensionName()).isEqualTo("2024");
        assertThat(response.getContent()).hasSize(2);
        
        assertThat(response.getContent().get(0).getArtistId()).isEqualTo(GUCCI_MANE_ID);
        assertThat(response.getContent().get(0).getArtistName()).isEqualTo(GUCCI_MANE_NAME);
        assertThat(response.getContent().get(0).getAlbumCount()).isEqualTo(GUCCI_2024_ALBUM_COUNT);
        
        assertThat(response.getContent().get(1).getArtistId()).isEqualTo(LIL_WAYNE_ID);
        assertThat(response.getContent().get(1).getArtistName()).isEqualTo(LIL_WAYNE_NAME);
        assertThat(response.getContent().get(1).getAlbumCount()).isEqualTo(WAYNE_2024_ALBUM_COUNT);
        
        assertThat(response.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(response.getPageable().getPageSize()).isEqualTo(10);
        assertThat(response.getPageable().getTotalElements()).isEqualTo(2L);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("아티스트가 없는 경우 - Unknown Artist 반환")
  void analyze_WithUnknownArtist_ShouldReturnUnknownArtist() {
    // Given
    AlbumAnnualAggregation agg = AlbumAnnualAggregation.builder()
      .releaseYear(YEAR_2024)
      .artistId(UNKNOWN_ARTIST_ID)
      .albumCount(5)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    when(aggregationRepository.findByReleaseYear(eq(YEAR_2024), eq(pageable)))
      .thenReturn(Flux.just(agg));
    when(aggregationRepository.countArtistsByYear(eq(YEAR_2024)))
      .thenReturn(Mono.just(1L));
    when(artistRepository.findById(UNKNOWN_ARTIST_ID))
      .thenReturn(Mono.empty());

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.YEAR, dimensionKey, pageable))
      .assertNext(response -> {
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getArtistId()).isEqualTo(UNKNOWN_ARTIST_ID);
        assertThat(response.getContent().get(0).getArtistName()).isEqualTo(UNKNOWN_ARTIST_NAME);
        assertThat(response.getContent().get(0).getAlbumCount()).isEqualTo(5);
      })
      .verifyComplete();
  }

  @Test
  @DisplayName("데이터가 없는 연도 조회 - 빈 결과 반환")
  void analyze_WithNoData_ShouldReturnEmptyResult() {
    // Given
    Integer emptyYear = 2020;

    when(aggregationRepository.findByReleaseYear(eq((short) 2020), eq(pageable)))
      .thenReturn(Flux.empty());
    when(aggregationRepository.countArtistsByYear(eq((short) 2020)))
      .thenReturn(Mono.just(0L));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.YEAR, emptyYear, pageable))
      .assertNext(response -> {
        assertThat(response.getDimensionKey()).isEqualTo(2020);
        assertThat(response.getDimensionName()).isEqualTo("2020");
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
    Pageable secondPage = PageRequest.of(1, 2);
    AlbumAnnualAggregation agg1 = AlbumAnnualAggregation.builder()
      .releaseYear(YEAR_2024)
      .artistId(YOUNG_THUG_ID)
      .albumCount(THUG_2024_ALBUM_COUNT)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();

    when(aggregationRepository.findByReleaseYear(eq((short) 2024), eq(secondPage)))
      .thenReturn(Flux.just(agg1));
    when(aggregationRepository.countArtistsByYear(eq((short) 2024)))
      .thenReturn(Mono.just(5L)); // 총 5개 데이터
    when(artistRepository.findById(YOUNG_THUG_ID))
      .thenReturn(Mono.just(YOUNG_THUG));

    // When & Then
    StepVerifier.create(service.analyze(AnalyticsDimension.YEAR, dimensionKey, secondPage))
      .assertNext(response -> {
        assertThat(response.getPageable().getPageNumber()).isEqualTo(1);
        assertThat(response.getPageable().getPageSize()).isEqualTo(2);
        assertThat(response.getPageable().getTotalElements()).isEqualTo(5L);
        assertThat(response.getPageable().getTotalPages()).isEqualTo(3); // 5개 데이터, 페이지당 2개
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
      })
      .verifyComplete();
  }

}
