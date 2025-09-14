package com.example.music.application.service;

import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.presentation.model.request.AlbumAnnualAnalyticsRequest;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumAnalyticsService {

  private final DimensionAlbumAnalyticsProvider analyticsProvider;

  public Mono<AlbumAnnualAnalyticsResponse> getAnnualAnalytics(AlbumAnnualAnalyticsRequest request) {
    AnalyticsDimension dimension = request.getDimension();
    Integer dimensionKey = request.getDimensionKey();
    Pageable pageable = request.toPageable();

    DimensionAlbumAnalyticsService analyticsService = analyticsProvider.getService(dimension);
    return analyticsService.analyze(dimension, dimensionKey, pageable)
      .doOnSuccess(response ->
        log.info("Successfully analyzed album data with dimension: {}, key: {}", dimension, dimensionKey)
      )
      .doOnError(error ->
        log.error("Failed to analyze album data with dimension: {}, key: {}", dimension, dimensionKey, error)
      );
  }

}
