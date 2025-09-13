package com.example.music.fixture;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonDataLoaderTestFixture {

  public static final String DEFAULT_RESOURCE_PATH = "classpath:test-data.json";
  public static final int DEFAULT_BATCH_SIZE = 10;
  public static final int SMALL_BATCH_SIZE = 1;
  
  public static final String TEST_JSON_DATA = "{\"Artist(s)\":\"!!!\",\"song\":\"Even When the Waters Cold\",\"text\":\"Friends told her she was better off at the bottom of a river Than in a bed with him He said \\\"Until you try both, you won't know what you like better Why don't we go for a swim?\\\" Well, friends told her this and friends told her that But friends don't choose what echoes in your head When she got bored with all the idle chit-and-chat Kept thinking 'bout what he said I'll swim even when the water's cold That's the one thing that I know Even when the water's cold She remembers it fondly, she doesn't remember it all But what she does, she sees clearly She lost his number, and he never called But all she really lost was an earring The other's in a box with others she has lost I wonder if she still hears me I'll swim even when the water's cold That's the one thing that I know Even when the water's cold If you believe in love You know that sometimes it isn't Do you believe in love? Then save the bullshit questions Sometimes it is and sometimes it isn't Sometimes it's just how the light hits their eyes Do you believe in love?\",\"Length\":\"03:47\",\"emotion\":\"sadness\",\"Genre\":\"hip hop\",\"Album\":\"Thr!!!er\",\"Release Date\":\"2013-04-29\",\"Key\":\"D min\",\"Tempo\":0.4378698225,\"Loudness (db)\":0.785065407,\"Time signature\":\"4/4\",\"Explicit\":\"No\",\"Popularity\":\"40\",\"Energy\":\"83\",\"Danceability\":\"71\",\"Positiveness\":\"87\",\"Speechiness\":\"4\",\"Liveness\":\"16\",\"Acousticness\":\"11\",\"Instrumentalness\":\"0\",\"Good for Party\":0,\"Good for Work/Study\":0,\"Good for Relaxation/Meditation\":0,\"Good for Exercise\":0,\"Good for Running\":0,\"Good for Yoga/Stretching\":0,\"Good for Driving\":0,\"Good for Social Gatherings\":0,\"Good for Morning Routine\":0,\"Similar Songs\":[{\"Similar Artist 1\":\"Corey Smith\",\"Similar Song 1\":\"If I Could Do It Again\",\"Similarity Score\":0.9860607848},{\"Similar Artist 2\":\"Toby Keith\",\"Similar Song 2\":\"Drinks After Work\",\"Similarity Score\":0.9837194774},{\"Similar Artist 3\":\"Space\",\"Similar Song 3\":\"Neighbourhood\",\"Similarity Score\":0.9832363508}]}";

  // Expected values
  public static final String EXPECTED_ARTIST_NAME = "!!!";
  public static final String EXPECTED_ALBUM_NAME = "Thr!!!er";
  public static final String EXPECTED_SONG_TITLE = "Even When the Waters Cold";
  public static final String EXPECTED_RELEASE_DATE = "2013-04-29";
  public static final byte EXPECTED_POPULARITY = 40;
  
  // Test data generators
  public static String createMultiArtistData() {
    return TEST_JSON_DATA.replace("\"!!!\"", "\"Artist1, Artist2, Artist3\"");
  }
  
  public static String createSecondRecordData() {
    return TEST_JSON_DATA
      .replace("\"Artist(s)\":\"!!!\"", "\"Artist(s)\":\"Artist2\"")
      .replace("\"song\":\"Even When the Waters Cold\"", "\"song\":\"Song2\"")
      .replace("\"Album\":\"Thr!!!er\"", "\"Album\":\"Album2\"");
  }
  
  public static String createDataWithEmptyLines() {
    return "\n\n" + TEST_JSON_DATA + "\n\n";
  }
  
  public static String createDataWithInvalidJson() {
    return "invalid json line\n" + TEST_JSON_DATA;
  }
  
  public static String createDataWithNullValues() {
    return TEST_JSON_DATA
      .replace("\"Release Date\":\"2013-04-29\"", "\"Release Date\":null")
      .replace("\"Popularity\":\"40\"", "\"Popularity\":null");
  }
  
  public static String createMultiLineData() {
    return TEST_JSON_DATA + "\n" + createSecondRecordData();
  }
  
  // Resource creation helper
  public static Resource createMockResource(String data) {
    return new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8)) {
      @Override
      public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
      }
    };
  }
  
  public static Resource createMockResourceWithError() {
    return new ByteArrayResource("".getBytes(StandardCharsets.UTF_8)) {
      @Override
      public InputStream getInputStream() throws IOException {
        throw new IOException("Resource not found");
      }
    };
  }

}
