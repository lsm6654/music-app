CREATE TABLE artists
(
    artist_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    artist_name VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE albums
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    album_name   VARCHAR(255) NOT NULL,
    release_date DATE         NOT NULL,
    release_year SMALLINT,
    total_songs  INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY          idx_album_release_year (release_year)
);

CREATE TABLE album_artist_mappings
(
    album_id   BIGINT NOT NULL,
    artist_id  BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (album_id, artist_id),
    KEY        idx_artist (artist_id),
    KEY        idx_is_primary (is_primary)
);


CREATE TABLE songs
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    album_id         BIGINT       NOT NULL,
    song_title       VARCHAR(255) NOT NULL,
    danceability     DECIMAL(4, 3),
    energy           DECIMAL(4, 3),
    positiveness     DECIMAL(4, 3),
    liveness         DECIMAL(4, 3),
    instrumentalness DECIMAL(6, 5),
    popularity       TINYINT,
    KEY              idx_album (album_id)
);

CREATE TABLE album_annual_aggregations
(
    release_year SMALLINT NOT NULL,
    artist_id    BIGINT   NOT NULL,
    album_count  INT      NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (release_year, artist_id),
    KEY          idx_artist (artist_id),
    KEY          idx_year (release_year)
);
