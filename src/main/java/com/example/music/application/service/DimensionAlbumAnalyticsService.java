package com.example.music.application.service;

import com.example.music.domain.enums.AnalyticsDimension;
import com.example.music.presentation.model.response.AlbumAnnualAnalyticsResponse;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface DimensionAlbumAnalyticsService {

  AnalyticsDimension getSupportedDimension();

  Mono<AlbumAnnualAnalyticsResponse> analyze(AnalyticsDimension dimension, Integer dimensionKey, Pageable pageable);

}
