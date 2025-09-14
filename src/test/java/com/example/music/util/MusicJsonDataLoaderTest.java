package com.example.music.util;

import com.example.music.domain.entity.Album;
import com.example.music.domain.entity.AlbumArtistMapping;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.entity.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.music.fixture.JsonDataLoaderTestFixture.*;
import com.example.music.fixture.JsonDataLoaderTestFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.springframework.core.io.ByteArrayResource;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsonDataLoader 테스트")
class MusicJsonDataLoaderTest {

  @Mock
  private ResourceLoader resourceLoader;

  private MusicJsonDataLoader musicJsonDataLoader;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    musicJsonDataLoader = new MusicJsonDataLoader(resourceLoader, objectMapper);
  }

  @Nested
  @DisplayName("정상 데이터 처리 테스트")
  class NormalDataProcessingTest {

    @Test
    @DisplayName("단일 레코드를 정상적으로 처리한다")
    void testLoadDataInBatches_SingleRecord() throws IOException {
      // Given
      Resource mockResource = JsonDataLoaderTestFixture.VALID_RESOURCE;
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, DEFAULT_BATCH_SIZE);

      // Then
      StepVerifier.create(result)
        .assertNext(musicData -> {
          assertSingleRecordData(musicData);
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("여러 아티스트가 있는 노래를 정상적으로 처리한다")
    void testLoadDataInBatches_MultipleArtists() throws IOException {
      // Given
      String multiArtistData = JsonDataLoaderTestFixture.VALID_JSON_RECORD.replace("\"!!!\"", "\"Artist1, Artist2, Artist3\"");
      Resource mockResource = new ByteArrayResource(multiArtistData.getBytes()) {
        @Override
        public java.io.InputStream getInputStream() {
          return new java.io.ByteArrayInputStream(getByteArray());
        }
      };
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, DEFAULT_BATCH_SIZE);

      // Then
      StepVerifier.create(result)
        .assertNext(musicData -> {
          assertMultipleArtistsData(musicData);
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("null 값이 포함된 데이터를 정상적으로 처리한다")
    void testLoadDataInBatches_NullValues() throws IOException {
      // Given
      Resource mockResource = new ByteArrayResource(JsonDataLoaderTestFixture.JSON_WITH_NULL_VALUES.getBytes()) {
        @Override
        public java.io.InputStream getInputStream() {
          return new java.io.ByteArrayInputStream(getByteArray());
        }
      };
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, DEFAULT_BATCH_SIZE);

      // Then
      StepVerifier.create(result)
        .assertNext(musicData -> {
          assertNullValuesHandling(musicData);
        })
        .verifyComplete();
    }
  }

  @Nested
  @DisplayName("배치 처리 테스트")
  class BatchProcessingTest {

    @Test
    @DisplayName("여러 레코드를 배치 크기에 따라 나누어 처리한다")
    void testLoadDataInBatches_MultipleRecordsWithBatching() throws IOException {
      // Given
      Resource mockResource = JsonDataLoaderTestFixture.MULTI_LINE_RESOURCE;
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, SMALL_BATCH_SIZE);

      // Then
      StepVerifier.create(result)
        .assertNext(firstBatch -> {
          assertFirstBatchData(firstBatch);
        })
        .assertNext(secondBatch -> {
          assertSecondBatchData(secondBatch);
        })
        .verifyComplete();
    }
  }

  @Nested
  @DisplayName("예외 상황 처리 테스트")
  class ExceptionHandlingTest {

    @Test
    @DisplayName("빈 줄이 포함된 데이터를 처리할 때 빈 줄을 무시한다")
    void testLoadDataInBatches_EmptyLines() throws IOException {
      // Given
      Resource mockResource = new ByteArrayResource(JsonDataLoaderTestFixture.RECORDS_WITH_EMPTY_LINES.getBytes()) {
        @Override
        public java.io.InputStream getInputStream() {
          return new java.io.ByteArrayInputStream(getByteArray());
        }
      };
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, DEFAULT_BATCH_SIZE);

      // Then
      // RECORDS_WITH_EMPTY_LINES는 2개의 유효한 레코드를 포함하므로
      // 빈 줄은 무시되고 2개의 레코드가 모두 처리되어야 함
      StepVerifier.create(result)
        .assertNext(musicData -> {
          // 첫 번째 레코드와 두 번째 레코드가 함께 배치로 처리됨
          assertThat(musicData.getArtists()).hasSize(2);
          assertThat(musicData.getAlbums()).hasSize(2);
          assertThat(musicData.getSongs()).hasSize(2);
          assertThat(musicData.getAlbumArtistMappings()).hasSize(2);
          
          // 첫 번째 아티스트 확인
          assertThat(musicData.getArtists().get(0).getArtistName()).isEqualTo(JsonDataLoaderTestFixture.ARTIST_NAME_SINGLE);
          // 두 번째 아티스트 확인
          assertThat(musicData.getArtists().get(1).getArtistName()).isEqualTo("Artist2");
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("유효하지 않은 JSON 라인을 무시하고 유효한 데이터만 처리한다")
    void testLoadDataInBatches_InvalidJsonLine() throws IOException {
      // Given
      Resource mockResource = new ByteArrayResource(JsonDataLoaderTestFixture.RECORDS_WITH_INVALID_LINE.getBytes()) {
        @Override
        public java.io.InputStream getInputStream() {
          return new java.io.ByteArrayInputStream(getByteArray());
        }
      };
      when(resourceLoader.getResource(DEFAULT_RESOURCE_PATH)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(DEFAULT_RESOURCE_PATH, DEFAULT_BATCH_SIZE);

      // Then
      // RECORDS_WITH_INVALID_LINE은 잘못된 줄 + 2개의 유효한 레코드를 포함
      // 잘못된 줄은 무시되고 2개의 유효한 레코드만 처리되어야 함
      StepVerifier.create(result)
        .assertNext(musicData -> {
          // 2개의 유효한 레코드가 배치로 처리됨
          assertThat(musicData.getArtists()).hasSize(2);
          assertThat(musicData.getAlbums()).hasSize(2);
          assertThat(musicData.getSongs()).hasSize(2);
          assertThat(musicData.getAlbumArtistMappings()).hasSize(2);
          
          // 첫 번째 아티스트 확인
          assertThat(musicData.getArtists().get(0).getArtistName()).isEqualTo(JsonDataLoaderTestFixture.ARTIST_NAME_SINGLE);
          // 두 번째 아티스트 확인  
          assertThat(musicData.getArtists().get(1).getArtistName()).isEqualTo("Artist2");
        })
        .verifyComplete();
    }

    @Test
    @DisplayName("리소스를 찾을 수 없을 때 IOException을 발생시킨다")
    void testLoadDataInBatches_ResourceNotFound() throws IOException {
      // Given
      String resourcePath = "classpath:non-existent.json";
      Resource mockResource = JsonDataLoaderTestFixture.ERROR_RESOURCE;
      when(resourceLoader.getResource(resourcePath)).thenReturn(mockResource);

      // When
      Flux<MusicJsonDataLoader.MusicData> result = musicJsonDataLoader.loadDataInBatches(resourcePath, DEFAULT_BATCH_SIZE);

      // Then
      StepVerifier.create(result)
        .expectError(IOException.class)
        .verify();
    }
  }

  // Assertion helper methods
  private void assertSingleRecordData(MusicJsonDataLoader.MusicData musicData) {
    // Artists 검증
    assertThat(musicData.getArtists()).hasSize(1);
    Artist artist = musicData.getArtists().get(0);
    assertThat(artist.getArtistName()).isEqualTo(JsonDataLoaderTestFixture.ARTIST_NAME_SINGLE);
    assertThat(artist.getArtistId()).isEqualTo(1L);
    assertThat(artist.getCreatedAt()).isNotNull();
    assertThat(artist.getUpdatedAt()).isNotNull();

    // Albums 검증
    assertThat(musicData.getAlbums()).hasSize(1);
    Album album = musicData.getAlbums().get(0);
    assertThat(album.getAlbumName()).isEqualTo(JsonDataLoaderTestFixture.ALBUM_NAME);
    assertThat(album.getReleaseDate()).isEqualTo(LocalDate.parse(JsonDataLoaderTestFixture.RELEASE_DATE));
    assertThat(album.getId()).isEqualTo(1L);
    assertThat(album.getCreatedAt()).isNotNull();
    assertThat(album.getUpdatedAt()).isNotNull();

    // Songs 검증
    assertThat(musicData.getSongs()).hasSize(1);
    Song song = musicData.getSongs().get(0);
    assertThat(song.getSongTitle()).isEqualTo(JsonDataLoaderTestFixture.SONG_TITLE);
    assertThat(song.getAlbum()).isEqualTo(album);
    assertThat(song.getDanceability()).isEqualTo(new BigDecimal("71"));
    assertThat(song.getEnergy()).isEqualTo(new BigDecimal("83"));
    assertThat(song.getPositiveness()).isEqualTo(new BigDecimal("87"));
    assertThat(song.getLiveness()).isEqualTo(new BigDecimal("16"));
    assertThat(song.getInstrumentalness()).isEqualTo(new BigDecimal("0"));
    assertThat(song.getPopularity()).isEqualTo(JsonDataLoaderTestFixture.POPULARITY);
    assertThat(song.getId()).isEqualTo(1L);

    // Album-Artist Mappings 검증
    assertThat(musicData.getAlbumArtistMappings()).hasSize(1);
    AlbumArtistMapping mapping = musicData.getAlbumArtistMappings().get(0);
    assertThat(mapping.getAlbum()).isEqualTo(album);
    assertThat(mapping.getArtist()).isEqualTo(artist);
    assertThat(mapping.getCreatedAt()).isNotNull();
    assertThat(mapping.getUpdatedAt()).isNotNull();
  }

  private void assertMultipleArtistsData(MusicJsonDataLoader.MusicData musicData) {
    // 3명의 아티스트가 생성되어야 함
    assertThat(musicData.getArtists()).hasSize(3);
    assertThat(musicData.getArtists().get(0).getArtistName()).isEqualTo("Artist1");
    assertThat(musicData.getArtists().get(1).getArtistName()).isEqualTo("Artist2");
    assertThat(musicData.getArtists().get(2).getArtistName()).isEqualTo("Artist3");
    
    // 3개의 Album-Artist 매핑이 생성되어야 함
    assertThat(musicData.getAlbumArtistMappings()).hasSize(3);
    
    // 모든 매핑이 같은 앨범을 참조해야 함
    Album album = musicData.getAlbums().get(0);
    for (AlbumArtistMapping mapping : musicData.getAlbumArtistMappings()) {
      assertThat(mapping.getAlbum()).isEqualTo(album);
    }
  }

  private void assertNullValuesHandling(MusicJsonDataLoader.MusicData musicData) {
    Album album = musicData.getAlbums().get(0);
    assertThat(album.getReleaseDate()).isNull();
    
    Song song = musicData.getSongs().get(0);
    assertThat(song.getPopularity()).isNull();
  }

  private void assertFirstBatchData(MusicJsonDataLoader.MusicData batch) {
    assertThat(batch.getArtists()).hasSize(1);
    assertThat(batch.getAlbums()).hasSize(1);
    assertThat(batch.getSongs()).hasSize(1);
    assertThat(batch.getAlbumArtistMappings()).hasSize(1);
    
    assertThat(batch.getArtists().get(0).getArtistName()).isEqualTo(JsonDataLoaderTestFixture.ARTIST_NAME_SINGLE);
    assertThat(batch.getAlbums().get(0).getAlbumName()).isEqualTo(JsonDataLoaderTestFixture.ALBUM_NAME);
    assertThat(batch.getSongs().get(0).getSongTitle()).isEqualTo(JsonDataLoaderTestFixture.SONG_TITLE);
  }

  private void assertSecondBatchData(MusicJsonDataLoader.MusicData batch) {
    assertThat(batch.getArtists()).hasSize(1);
    assertThat(batch.getAlbums()).hasSize(1);
    assertThat(batch.getSongs()).hasSize(1);
    assertThat(batch.getAlbumArtistMappings()).hasSize(1);
    
    assertThat(batch.getArtists().get(0).getArtistName()).isEqualTo("Artist2");
    assertThat(batch.getAlbums().get(0).getAlbumName()).isEqualTo("Album2");
    assertThat(batch.getSongs().get(0).getSongTitle()).isEqualTo("Song2");
  }

  private void assertSingleDataPresence(MusicJsonDataLoader.MusicData musicData) {
    assertThat(musicData.getArtists()).hasSize(1);
    assertThat(musicData.getAlbums()).hasSize(1);
    assertThat(musicData.getSongs()).hasSize(1);
    assertThat(musicData.getAlbumArtistMappings()).hasSize(1);
  }

}
