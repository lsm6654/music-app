package com.example.music.application.service;

import com.example.music.domain.enums.AnalyticsDimension;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DimensionAlbumAnalyticsProvider {

  private final Map<AnalyticsDimension, DimensionAlbumAnalyticsService> services;
  
  @Autowired
  public DimensionAlbumAnalyticsProvider(List<DimensionAlbumAnalyticsService> serviceList) {
    this.services = serviceList.stream()
      .collect(Collectors.toMap(
        DimensionAlbumAnalyticsService::getSupportedDimension,
        Function.identity()
      ));
  }

  public DimensionAlbumAnalyticsService getService(AnalyticsDimension dimension) {
    DimensionAlbumAnalyticsService service = services.get(dimension);
    
    if (service == null) {
      throw new IllegalArgumentException("Unsupported dimension: " + dimension);
    }
    
    return service;
  }

}
