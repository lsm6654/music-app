package com.example.music.infrastructure.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RedisKeyGenerator 테스트")
class RedisKeyGeneratorTest {

  private final RedisKeyGenerator redisKeyGenerator = new RedisKeyGenerator();


  @Test
  @DisplayName("오전 시간")
  void generateMinuteKey_Morning() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 9, 30, 0);

    String key = redisKeyGenerator.generateMinuteKey(dateTime);

    assertThat(key).isEqualTo("songs:v1:20240115:570:likes");
  }

  @Test
  @DisplayName("자정")
  void generateMinuteKey_Midnight() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 3, 1, 0, 0, 0);

    String key = redisKeyGenerator.generateMinuteKey(dateTime);

    assertThat(key).isEqualTo("songs:v1:20240301:0:likes");
  }

  @Test
  @DisplayName("하루 마지막 시간")
  void generateMinuteKey_EndOfDay() {
    LocalDateTime dateTime = LocalDateTime.of(2024, 6, 30, 23, 59, 59);

    String key = redisKeyGenerator.generateMinuteKey(dateTime);

    assertThat(key).isEqualTo("songs:v1:20240630:1439:likes");
  }

}
