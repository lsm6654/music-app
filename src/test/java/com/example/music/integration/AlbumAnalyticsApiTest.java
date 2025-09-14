package com.example.music.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("AlbumAnalytics API 통합 테스트")
class AlbumAnalyticsApiTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  @DisplayName("정상적인 요청 - 연도별 차원")
  void getAnnualAnalytics_ValidRequest_Year() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 2023)
        .queryParam("page", 0)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.dimension").isEqualTo("YEAR")
      .jsonPath("$.dimensionKey").isEqualTo(2023)
      .jsonPath("$.content").isArray()
      .jsonPath("$.pageable.pageNumber").isEqualTo(0)
      .jsonPath("$.pageable.pageSize").isEqualTo(10);
  }

  @Test
  @DisplayName("정상적인 요청 - 아티스트별 차원")
  void getAnnualAnalytics_ValidRequest_Artist() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "ARTIST_ID")
        .queryParam("dimensionKey", 1)  // 실제 존재하는 아티스트 ID 사용
        .queryParam("page", 0)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.dimension").isEqualTo("ARTIST_ID")
      .jsonPath("$.dimensionKey").isEqualTo(1)
      .jsonPath("$.content").isArray()
      .jsonPath("$.pageable.pageNumber").isEqualTo(0)
      .jsonPath("$.pageable.pageSize").isEqualTo(10);
  }

  @Test
  @DisplayName("잘못된 dimension 값으로 요청 시 400 에러")
  void getAnnualAnalytics_InvalidDimension_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "INVALID")
        .queryParam("dimensionKey", 2023)
        .queryParam("page", 1)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("dimensionKey 파라미터 누락 시 400 에러")
  void getAnnualAnalytics_MissingDimensionKey_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("page", 1)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("dimensionKey가 1 미만일 때 400 에러")
  void getAnnualAnalytics_DimensionKeyTooSmall_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 0)
        .queryParam("page", 1)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("dimensionKey가 0일 때 400 에러")
  void getAnnualAnalytics_DimensionKeyZero_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 0)
        .queryParam("page", 1)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("page 파라미터가 -1일 때 400 에러")
  void getAnnualAnalytics_InvalidPage_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 2023)
        .queryParam("page", -1)
        .queryParam("size", 10)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("size 파라미터가 101일 때 400 에러")
  void getAnnualAnalytics_InvalidSize_ShouldReturn400() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 2023)
        .queryParam("page", 1)
        .queryParam("size", 101)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.status").isEqualTo(400)
      .jsonPath("$.error").isEqualTo("001")
      .jsonPath("$.message").isEqualTo("잘못된 요청입니다.");
  }

  @Test
  @DisplayName("기본값 사용 - page와 size 생략")
  void getAnnualAnalytics_DefaultValues() {
    webTestClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/api/v1/albums/analytics/annual")
        .queryParam("dimension", "YEAR")
        .queryParam("dimensionKey", 2023)
        .build())
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.dimension").isEqualTo("YEAR")
      .jsonPath("$.dimensionKey").isEqualTo(2023)
      .jsonPath("$.pageable.pageNumber").isEqualTo(0)
      .jsonPath("$.pageable.pageSize").isEqualTo(20);  // 기본값이 20입니다
  }

}
