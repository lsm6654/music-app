package com.example.music.fixture;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON 데이터 로더 테스트용 Fixture
 * 테스트에 필요한 고정된 JSON 데이터와 리소스를 제공합니다.
 */
public class JsonDataLoaderTestFixture {

  public static final String DEFAULT_RESOURCE_PATH = "classpath:test-data.json";
  public static final int DEFAULT_BATCH_SIZE = 10;
  public static final int SMALL_BATCH_SIZE = 1;
  public static final int LARGE_BATCH_SIZE = 100;

  public static final String ARTIST_NAME_SINGLE = "!!!";
  public static final String ARTIST_NAME_MULTI = "Artist1, Artist2, Artist3";
  public static final String ARTIST_NAME_SECOND = "Artist2";

  public static final String ALBUM_NAME = "Thr!!!er";
  public static final String ALBUM_NAME_SECOND = "Album2";
  public static final String ALBUM_TYPE = "hip hop";
  public static final String RELEASE_DATE = "2013-04-29";

  public static final String SONG_TITLE = "Even When the Waters Cold";
  public static final String SONG_TITLE_SECOND = "Song2";
  public static final String SONG_LENGTH = "03:47";
  public static final String SONG_KEY = "D min";
  public static final String TIME_SIGNATURE = "4/4";

  public static final byte POPULARITY = 40;
  public static final int ENERGY = 83;
  public static final int DANCEABILITY = 71;
  public static final int POSITIVENESS = 87;
  public static final int SPEECHINESS = 4;
  public static final int LIVENESS = 16;
  public static final int ACOUSTICNESS = 11;
  public static final int INSTRUMENTALNESS = 0;
  public static final double TEMPO = 0.4378698225;
  public static final double LOUDNESS = 0.785065407;

  // 완전한 JSON 레코드 (정상 데이터)
  public static final String VALID_JSON_RECORD = """
    {"Artist(s)":"!!!","song":"Even When the Waters Cold","text":"Friends told her she was better off at the bottom of a river Than in a bed with him He said \\"Until you try both, you won't know what you like better Why don't we go for a swim?\\" Well, friends told her this and friends told her that But friends don't choose what echoes in your head When she got bored with all the idle chit-and-chat Kept thinking 'bout what he said I'll swim even when the water's cold That's the one thing that I know Even when the water's cold She remembers it fondly, she doesn't remember it all But what she does, she sees clearly She lost his number, and he never called But all she really lost was an earring The other's in a box with others she has lost I wonder if she still hears me I'll swim even when the water's cold That's the one thing that I know Even when the water's cold If you believe in love You know that sometimes it isn't Do you believe in love? Then save the bullshit questions Sometimes it is and sometimes it isn't Sometimes it's just how the light hits their eyes Do you believe in love?","Length":"03:47","emotion":"sadness","Genre":"hip hop","Album":"Thr!!!er","Release Date":"2013-04-29","Key":"D min","Tempo":0.4378698225,"Loudness (db)":0.785065407,"Time signature":"4/4","Explicit":"No","Popularity":"40","Energy":"83","Danceability":"71","Positiveness":"87","Speechiness":"4","Liveness":"16","Acousticness":"11","Instrumentalness":"0","Good for Party":0,"Good for Work/Study":0,"Good for Relaxation/Meditation":0,"Good for Exercise":0,"Good for Running":0,"Good for Yoga/Stretching":0,"Good for Driving":0,"Good for Social Gatherings":0,"Good for Morning Routine":0,"Similar Songs":[{"Similar Artist 1":"Corey Smith","Similar Song 1":"If I Could Do It Again","Similarity Score":0.9860607848},{"Similar Artist 2":"Toby Keith","Similar Song 2":"Drinks After Work","Similarity Score":0.9837194774},{"Similar Artist 3":"Space","Similar Song 3":"Neighbourhood","Similarity Score":0.9832363508}]}""".trim();

  // 다중 아티스트 JSON 레코드
  public static final String MULTI_ARTIST_JSON_RECORD = VALID_JSON_RECORD
    .replace("\"!!!\"", "\"Artist1, Artist2, Artist3\"");

  // 두 번째 JSON 레코드 (테스트용)
  public static final String SECOND_JSON_RECORD = VALID_JSON_RECORD
    .replace("\"Artist(s)\":\"!!!\"", "\"Artist(s)\":\"Artist2\"")
    .replace("\"song\":\"Even When the Waters Cold\"", "\"song\":\"Song2\"")
    .replace("\"Album\":\"Thr!!!er\"", "\"Album\":\"Album2\"");

  // NULL 값이 포함된 JSON 레코드
  public static final String JSON_WITH_NULL_VALUES = VALID_JSON_RECORD
    .replace("\"Release Date\":\"2013-04-29\"", "\"Release Date\":null")
    .replace("\"Popularity\":\"40\"", "\"Popularity\":null");

  // 잘못된 JSON 데이터
  public static final String INVALID_JSON_LINE = "invalid json line";
  public static final String MALFORMED_JSON = "{\"Artist(s)\":\"Test\","; // 불완전한 JSON

  // 빈 데이터
  public static final String EMPTY_LINE = "";
  public static final String WHITESPACE_LINE = "   ";

  // 여러 줄 데이터 조합
  public static final String TWO_RECORDS = VALID_JSON_RECORD + "\n" + SECOND_JSON_RECORD;
  public static final String RECORDS_WITH_EMPTY_LINES = "\n\n" + VALID_JSON_RECORD + "\n\n" + SECOND_JSON_RECORD + "\n\n";
  public static final String RECORDS_WITH_INVALID_LINE = INVALID_JSON_LINE + "\n" + VALID_JSON_RECORD + "\n" + SECOND_JSON_RECORD;

  // Mock Resource 생성용 데이터
  public static final Resource VALID_RESOURCE = createResource(VALID_JSON_RECORD);
  public static final Resource MULTI_LINE_RESOURCE = createResource(TWO_RECORDS);
  public static final Resource EMPTY_RESOURCE = createResource("");
  public static final Resource ERROR_RESOURCE = createErrorResource();

  private static Resource createResource(String data) {
    return new ByteArrayResource(data.getBytes(StandardCharsets.UTF_8)) {
      @Override
      public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
      }

      @Override
      public String getFilename() {
        return "test-data.json";
      }
    };
  }

  private static Resource createErrorResource() {
    return new ByteArrayResource("".getBytes(StandardCharsets.UTF_8)) {
      @Override
      public InputStream getInputStream() throws IOException {
        throw new IOException("Resource not found");
      }

      @Override
      public String getFilename() {
        return "error-data.json";
      }
    };
  }

  private JsonDataLoaderTestFixture() {
    throw new UnsupportedOperationException("This is a fixture class and cannot be instantiated");
  }

}
