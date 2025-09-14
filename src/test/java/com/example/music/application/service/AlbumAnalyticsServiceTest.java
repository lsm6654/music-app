package com.example.music.application.service;

import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.presentation.model.request.AlbumAnnualAnalyticsRequest;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.example.music.fixture.AlbumAnalyticsTestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AlbumAnalyticsServiceTest {

  @Mock
  private DimensionAlbumAnalyticsProvider analyticsProvider;

  @Mock
  private DimensionAlbumAnalyticsService yearDimensionService;

  @Mock
  private DimensionAlbumAnalyticsService artistDimensionService;

  @InjectMocks
  private AlbumAnalyticsService service;

  @BeforeEach
  void setUp() {
    when(analyticsProvider.getService(AnalyticsDimension.YEAR))
      .thenReturn(yearDimensionService);
    when(analyticsProvider.getService(AnalyticsDimension.ARTIST_ID))
      .thenReturn(artistDimensionService);
  }

  @Test
  @DisplayName("연도별 분석 요청 처리")
  void getAnnualAnalytics_WithYearDimension_ShouldDelegateToYearService() {
    // Given - Fixture 사용
    AlbumAnnualAnalyticsRequest request = YEAR_2024_REQUEST;
    AlbumAnnualAnalyticsResponse expectedResponse = YEAR_2024_RESPONSE;

    when(yearDimensionService.analyze(
      eq(AnalyticsDimension.YEAR),
      eq(2024),
      any(Pageable.class)
    )).thenReturn(Mono.just(expectedResponse));

    // When & Then
    StepVerifier.create(service.getAnnualAnalytics(request))
      .assertNext(response -> {
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.getDimension()).isEqualTo(AnalyticsDimension.YEAR);
        assertThat(response.getDimensionKey()).isEqualTo(2024);
      })
      .verifyComplete();

    // Verify
    verify(analyticsProvider).getService(AnalyticsDimension.YEAR);
    verify(yearDimensionService).analyze(
      eq(AnalyticsDimension.YEAR),
      eq(2024),
      any(Pageable.class)
    );
    verifyNoInteractions(artistDimensionService);
  }

  @Test
  @DisplayName("아티스트별 분석 요청 처리")
  void getAnnualAnalytics_WithArtistDimension_ShouldDelegateToArtistService() {
    // Given - Fixture 사용
    AlbumAnnualAnalyticsRequest request = GUCCI_MANE_REQUEST;
    AlbumAnnualAnalyticsResponse expectedResponse = GUCCI_MANE_RESPONSE;

    when(artistDimensionService.analyze(
      eq(AnalyticsDimension.ARTIST_ID),
      eq(608),
      any(Pageable.class)
    )).thenReturn(Mono.just(expectedResponse));

    // When & Then
    StepVerifier.create(service.getAnnualAnalytics(request))
      .assertNext(response -> {
        assertThat(response).isEqualTo(expectedResponse);
        assertThat(response.getDimension()).isEqualTo(AnalyticsDimension.ARTIST_ID);
        assertThat(response.getDimensionKey()).isEqualTo(608);
      })
      .verifyComplete();

    // Verify
    verify(analyticsProvider).getService(AnalyticsDimension.ARTIST_ID);
    verify(artistDimensionService).analyze(
      eq(AnalyticsDimension.ARTIST_ID),
      eq(608),
      any(Pageable.class)
    );
    verifyNoInteractions(yearDimensionService);
  }

  @Test
  @DisplayName("서비스 에러 발생 시 에러 전파")
  void getAnnualAnalytics_WhenServiceThrowsError_ShouldPropagateError() {
    // Given - Fixture 사용
    AlbumAnnualAnalyticsRequest request = YEAR_2024_REQUEST;
    RuntimeException expectedException = new RuntimeException("Service error");

    when(yearDimensionService.analyze(
      any(AnalyticsDimension.class),
      any(Integer.class),
      any(Pageable.class)
    )).thenReturn(Mono.error(expectedException));

    // When & Then
    StepVerifier.create(service.getAnnualAnalytics(request))
      .expectError(RuntimeException.class)
      .verify();
  }

  @Test
  @DisplayName("페이징 파라미터 전달 확인")
  void getAnnualAnalytics_ShouldPassCorrectPageable() {
    // Given
    AlbumAnnualAnalyticsRequest request = AlbumAnnualAnalyticsRequest.builder()
      .dimension(AnalyticsDimension.YEAR)
      .dimensionKey(2024)
      .page(2)
      .size(50)
      .build();

    AlbumAnnualAnalyticsResponse expectedResponse = YEAR_2024_RESPONSE;

    when(yearDimensionService.analyze(
      eq(AnalyticsDimension.YEAR),
      eq(2024),
      any(Pageable.class)
    )).thenReturn(Mono.just(expectedResponse));

    // When
    StepVerifier.create(service.getAnnualAnalytics(request))
      .assertNext(response -> assertThat(response).isNotNull())
      .verifyComplete();

    // Then
    verify(yearDimensionService).analyze(
      eq(AnalyticsDimension.YEAR),
      eq(2024),
      argThat(pageable ->
        pageable.getPageNumber() == 2 &&
        pageable.getPageSize() == 50
      )
    );
  }

}
