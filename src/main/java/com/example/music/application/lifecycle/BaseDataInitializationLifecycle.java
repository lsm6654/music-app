package com.example.music.application.lifecycle;

import com.example.music.application.service.DataInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 서버 시작 전에 테스트 데이터를 DB 적재하는 라이프사이클 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BaseDataInitializationLifecycle implements SmartLifecycle {

  private final AtomicBoolean running = new AtomicBoolean(false);

  private final DataInitializationService dataInitializationService;

  @Override
  public void start() {
    log.info("Starting data initialization...");
    try {
       dataInitializationService.loadBaseData();
      log.info("Successfully initialized data.");
      running.set(true);
    } catch (Exception e) {
      running.set(false);
      throw new IllegalStateException("Failed to initialize required data. Server cannot start.", e);
    }
  }

  @Override
  public void stop() {
    running.set(false);
  }

  @Override
  public boolean isRunning() {
    return running.get();
  }

  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }

}
