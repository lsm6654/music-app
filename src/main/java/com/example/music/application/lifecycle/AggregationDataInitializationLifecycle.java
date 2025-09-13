package com.example.music.application.lifecycle;

import com.example.music.application.service.DataInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 서버 시작 전에 기초 데이터로 집계 데이터를 생성하는 라이프사이클 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationDataInitializationLifecycle implements SmartLifecycle {

  private final DataInitializationService dataInitializationService;
  private final AtomicBoolean running = new AtomicBoolean(false);

  @Override
  public void start() {
    log.info("Starting aggregation data initialization lifecycle...");

    try {
      // 집계 데이터 생성
      dataInitializationService.loadAggregationData();
      running.set(true);
      log.info("Aggregation data initialization lifecycle completed successfully.");
    } catch (Exception e) {
      log.error("Failed to initialize aggregation data in lifecycle", e);
      throw new RuntimeException("Failed to start aggregation data initialization lifecycle", e);
    }
  }

  @Override
  public void stop() {
    log.info("Stopping aggregation data initialization lifecycle...");
    running.set(false);
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public int getPhase() {
    // BaseDataInitializationLifecycle 이후에 실행
    return Integer.MIN_VALUE + 2;
  }

}
