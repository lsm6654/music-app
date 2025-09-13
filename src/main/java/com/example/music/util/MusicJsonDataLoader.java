package com.example.music.util;

import com.example.music.domain.entity.Album;
import com.example.music.domain.entity.AlbumArtistMapping;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.entity.Song;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicJsonDataLoader {

  private final ResourceLoader resourceLoader;
  private final ObjectMapper objectMapper;

  private final AtomicLong artistIdCounter = new AtomicLong(1);
  private final AtomicLong albumIdCounter = new AtomicLong(1);
  private final AtomicLong songIdCounter = new AtomicLong(1);

  @lombok.Value
  @lombok.Builder
  public static class MusicData {
    List<Artist> artists;
    List<Album> albums;
    List<Song> songs;
    List<AlbumArtistMapping> albumArtistMappings;
  }

  public Flux<MusicData> loadDataInBatches(String resourcePath, int batchSize) {
    return Flux.defer(() -> {
      try {
        Resource resource = resourceLoader.getResource(resourcePath);
        InputStream inputStream = resource.getInputStream();

        return streamJsonData(inputStream, batchSize)
          .doFinally(signal -> {
            try {
              inputStream.close();
            } catch (IOException e) {
              log.error("Error closing resources", e);
            }
          });
      } catch (IOException e) {
        return Flux.error(e);
      }
    }).subscribeOn(Schedulers.boundedElastic());
  }

  private Flux<MusicData> streamJsonData(InputStream inputStream, int batchSize) {
    return Flux.create(sink -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        List<Artist> batchArtists = new ArrayList<>();
        List<Album> batchAlbums = new ArrayList<>();
        List<Song> batchSongs = new ArrayList<>();
        List<AlbumArtistMapping> batchMappings = new ArrayList<>();
        int currentBatchCount = 0;

        String line;
        while ((line = reader.readLine()) != null) {
          if (line.trim().isEmpty()) {
            continue; // Skip empty lines
          }

          try {
            Map<String, Object> songData = objectMapper.readValue(line, Map.class);
            processSongData(songData, batchArtists, batchAlbums, batchSongs, batchMappings);
            currentBatchCount++;

            if (currentBatchCount >= batchSize) {
              sink.next(MusicData.builder()
                .artists(new ArrayList<>(batchArtists))
                .albums(new ArrayList<>(batchAlbums))
                .songs(new ArrayList<>(batchSongs))
                .albumArtistMappings(new ArrayList<>(batchMappings))
                .build());

              // Clear batch
              batchArtists.clear();
              batchAlbums.clear();
              batchSongs.clear();
              batchMappings.clear();
              currentBatchCount = 0;
            }
          } catch (Exception e) {
            log.warn("Failed to parse line: {}", line, e);
          }
        }

        if (!batchSongs.isEmpty()) {
          sink.next(MusicData.builder()
            .artists(new ArrayList<>(batchArtists))
            .albums(new ArrayList<>(batchAlbums))
            .songs(new ArrayList<>(batchSongs))
            .albumArtistMappings(new ArrayList<>(batchMappings))
            .build());
        }

        sink.complete();
      } catch (Exception e) {
        sink.error(e);
      }
    });
  }

  private void processSongData(
    Map<String, Object> data,
    List<Artist> batchArtists,
    List<Album> batchAlbums,
    List<Song> batchSongs,
    List<AlbumArtistMapping> batchMappings
  ) {
    String artistsStr = (String) data.get("Artist(s)");
    List<Artist> artists = parseArtists(artistsStr);
    batchArtists.addAll(artists);

    String albumName = (String) data.get("Album");
    String releaseDate = (String) data.get("Release Date");
    LocalDate parsedDate = parseReleaseDate(releaseDate);
    Album album = Album.builder()
      .id(albumIdCounter.getAndIncrement())
      .albumName(albumName)
      .releaseDate(parsedDate)
      .releaseYear(parsedDate != null ? (short) parsedDate.getYear() : null)
      .createdAt(LocalDateTime.now())
      .updatedAt(LocalDateTime.now())
      .build();
    batchAlbums.add(album);

    for (Artist artist : artists) {
      AlbumArtistMapping mapping = AlbumArtistMapping.builder()
        .album(album)
        .artist(artist)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
      batchMappings.add(mapping);
    }

    Song song = Song.builder()
      .id(songIdCounter.getAndIncrement())
      .songTitle((String) data.get("song"))
      .album(album)
      .danceability(parseBigDecimal(data.get("Danceability")))
      .energy(parseBigDecimal(data.get("Energy")))
      .positiveness(parseBigDecimal(data.get("Positiveness")))
      .liveness(parseBigDecimal(data.get("Liveness")))
      .instrumentalness(parseBigDecimal(data.get("Instrumentalness")))
      .popularity(parseByte(data.get("Popularity")))
      .build();
    batchSongs.add(song);
  }

  private List<Artist> parseArtists(String artistsStr) {
    List<Artist> artists = new ArrayList<>();
    if (artistsStr != null && !artistsStr.isEmpty()) {
      String[] artistNames = artistsStr.split(",");
      for (String name : artistNames) {
        String trimmedName = name.trim();
        if (!trimmedName.isEmpty()) {
          Artist artist = Artist.builder()
            .artistId(artistIdCounter.getAndIncrement())
            .artistName(trimmedName)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
          artists.add(artist);
        }
      }
    }
    return artists;
  }

  private LocalDate parseReleaseDate(String dateStr) {
    if (dateStr == null || dateStr.isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(dateStr);
    } catch (Exception e) {
      log.warn("Failed to parse date: {}", dateStr);
      return null;
    }
  }

  private BigDecimal parseBigDecimal(Object value) {
    if (value == null) return null;
    if (value instanceof Number) {
      return BigDecimal.valueOf(((Number) value).doubleValue());
    }
    try {
      return new BigDecimal(value.toString());
    } catch (Exception e) {
      return null;
    }
  }

  private Byte parseByte(Object value) {
    if (value == null) return null;
    if (value instanceof Number) {
      return ((Number) value).byteValue();
    }
    try {
      return Byte.parseByte(value.toString());
    } catch (Exception e) {
      return null;
    }
  }

}
