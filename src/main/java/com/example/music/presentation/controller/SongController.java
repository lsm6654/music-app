package com.example.music.presentation.controller;

import com.example.music.application.service.SongLikeService;
import com.example.music.presentation.model.request.SongLikeRequest;
import com.example.music.presentation.model.response.SongLikeResponse;
import com.example.music.presentation.model.response.TopTrendResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

  private final SongLikeService songLikeService;

  @PostMapping("/{songId}/likes")
  public Mono<ResponseEntity<SongLikeResponse>> addLike(
    @PathVariable String songId,
    @Valid @RequestBody(required = false) SongLikeRequest request
  ) {
    String userId = request.getUserId();

    return songLikeService.addLike(songId, userId)
      .thenReturn(ResponseEntity.ok(
        SongLikeResponse.builder()
          .success(true)
          .songId(songId)
          .build()
      ))
      .doOnError(error ->
        log.error("Error processing like request. request={}, cause={}", request, error.getCause(), error)
      );
  }

  @GetMapping("/likes/trends")
  public Mono<TopTrendResponse> getTrends() {
    return songLikeService.getTopTrends();
  }

}
