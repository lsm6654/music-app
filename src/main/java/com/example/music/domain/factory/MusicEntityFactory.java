package com.example.music.domain.factory;

import com.example.music.domain.entity.Album;
import com.example.music.domain.entity.AlbumArtistMapping;
import com.example.music.domain.entity.Artist;
import com.example.music.domain.entity.Song;
import org.springframework.stereotype.Component;

/**
 * 음악 관련 엔티티 생성을 담당하는 팩토리 클래스
 */
@Component
public class MusicEntityFactory {

  public Artist createArtist(Artist artistData) {
    return Artist.builder()
      .artistName(artistData.getArtistName())
      .createdAt(artistData.getCreatedAt())
      .updatedAt(artistData.getUpdatedAt())
      .build();
  }

  public Album createAlbum(Album albumData) {
    return Album.builder()
      .albumName(albumData.getAlbumName())
      .albumType(albumData.getAlbumType())
      .releaseDate(albumData.getReleaseDate())
      .releaseYear(albumData.getReleaseYear())
      .totalSongs(albumData.getTotalSongs())
      .createdAt(albumData.getCreatedAt())
      .updatedAt(albumData.getUpdatedAt())
      .build();
  }

  public Song createSong(Song songData, Long albumId) {
    return Song.builder()
      .songTitle(songData.getSongTitle())
      .albumId(albumId)
      .danceability(songData.getDanceability())
      .energy(songData.getEnergy())
      .positiveness(songData.getPositiveness())
      .liveness(songData.getLiveness())
      .instrumentalness(songData.getInstrumentalness())
      .popularity(songData.getPopularity())
      .build();
  }

  public AlbumArtistMapping createAlbumArtistMapping(
    AlbumArtistMapping mappingData,
    Long albumId,
    Long artistId) {
    return AlbumArtistMapping.builder()
      .albumId(albumId)
      .artistId(artistId)
      .createdAt(mappingData.getCreatedAt())
      .updatedAt(mappingData.getUpdatedAt())
      .build();
  }

}
