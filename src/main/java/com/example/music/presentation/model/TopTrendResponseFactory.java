package com.example.music.presentation.model;

import com.example.music.presentation.model.response.TopTrendResponse;
import com.example.music.presentation.model.response.TopTrendResponse.TrendItem;
import com.example.music.presentation.model.response.TopTrendResponse.TrendPeriod;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopTrendResponseFactory {

  public static TopTrendResponse createFrom(List<TypedTuple<String>> trendData) {
    LocalDateTime now = LocalDateTime.now();
    AtomicInteger rank = new AtomicInteger(1);

    List<TrendItem> trendItems = trendData.stream()
      .map(trend -> {
        Double score = trend.getScore();
        int likeCount = score != null ? score.intValue() : 0;

        return TrendItem.builder()
          .rank(rank.getAndIncrement())
          .songId(trend.getValue())
          .likeCount(likeCount)
          .build();
      })
      .toList();

    TrendPeriod period = TrendPeriod.builder()
      .start(now.minusHours(1))
      .end(now)
      .build();

    return TopTrendResponse.builder()
      .timestamp(now)
      .period(period)
      .trends(trendItems)
      .totalSongs(trendItems.size())
      .build();
  }

}
