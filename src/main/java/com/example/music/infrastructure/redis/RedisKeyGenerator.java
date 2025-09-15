package com.example.music.infrastructure.redis;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RedisKeyGenerator {

  private static final String SONGS_LIKES_KEY = "songs:v1:%s:%d:likes";         // songs:v1:{date}:{minutesOfDay}:likes
  private static final String TRENDS_LIKES_KEY = "songs:v1:%s:%d:likes:trends";  // songs:v1:{date}:{minutesOfDay}:likes:trends

  public String generateMinuteKey(LocalDateTime dateTime) {
    String date = formatDate(dateTime);
    int minutesOfDay = calculateMinutesOfDay(dateTime);
    return String.format(SONGS_LIKES_KEY, date, minutesOfDay);
  }

  public String generateTrendsKey(LocalDateTime dateTime) {
    String date = formatDate(dateTime);
    int minutesOfDay = calculateMinutesOfDay(dateTime);
    return String.format(TRENDS_LIKES_KEY, date, minutesOfDay);
  }

  private String formatDate(LocalDateTime dateTime) {
    return String.format("%04d%02d%02d",
      dateTime.getYear(),
      dateTime.getMonthValue(),
      dateTime.getDayOfMonth());
  }

  private int calculateMinutesOfDay(LocalDateTime dateTime) {
    return dateTime.getHour() * 60 + dateTime.getMinute();
  }

}
