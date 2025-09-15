# music-app project
https://github.com/lsm6654/music-app

어플리케이션 실행 명령어:
```shell
./gradlew bootRun
```

## Prerequisites
- Java 21
- data setup
  - https://www.kaggle.com/datasets/devdope/900k-spotify?select=900k+Definitive+Spotify+Dataset.json json 데이터를 resources/init-data/dataset.json 에 위치해야 함

## Technical stacks
- Java 21
- Spring Boot 
- Spring WebFlux
- Spring data r2dbc
- embedded-redis
- h2 db
- swagger

## Architecture
Layered architecture 와 DDD 를 활용하여 구성했습니다.

### H2DB
기초 데이터를 통해 aggregation table 을 두고, dimension(year, artist)별로 조회 가능하도록 데이터를 구성했습니다.
- [Album.java](src/main/java/com/example/music/domain/entity/Album.java)
- [AlbumArtistMapping.java](src/main/java/com/example/music/domain/entity/AlbumArtistMapping.java)
- [Artist.java](src/main/java/com/example/music/domain/entity/Artist.java)
- [Song.java](src/main/java/com/example/music/domain/entity/Song.java)
- [AlbumAnnualAggregation.java](src/main/java/com/example/music/domain/entity/AlbumAnnualAggregation.java)

좋아요 데이터는 이벤트 로그 기반으로 설계했습니다
- [EventLog.java](src/main/java/com/example/music/domain/entity/EventLog.java)

### Redis 
좋아요를 준 실시간으로 조회 가능하도록 Redis 에 1분 단위의 ZSET 을 구성하였고, 
좋아요에 대한 인기(Trend)를 조회할 수 있도록 ZSET 을 추가 구성하여 aggregation 하였습니다 (UNIONSTORE)

- songs:v1:{date}:{minutesOfDay}:likes
- songs:v1:{date}:{minutesOfDay}:likes:trends

## Problem solving strategy

### 초기 데이터 적재
어플리케이션이 뜨기 전에 기초 데이터 / 집계 데이터를 구성해야 하므로 Spring Lifecycle 을 구성하여 컨트롤 했습니다.
관심사에 맞추어 분리해놓았으며 순서 의존성을 따르도록 설정했습니다.

### Dimension 처리
연도별 / 가수별 데이터를 조회 가능하도록 서비스 레이어를 추상화하였습니다.

### 좋아요 API throttle
좋아요의 이벤트 발생시 Redis 부하 발생 방지를 위한 throttle 처리를 하여 트래픽을 대비하였습니다.

### 좋아요 near real-time 데이터 제공
좋아요에 대한 트렌드 데이터를 구성하기 위해 1분 단위의 ZSET 을 활용하여 데이터를 적재하였습니다. 
준실시간(1분 윈도우)의 데이터 제공이 가능합니다.
