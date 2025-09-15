CREATE TABLE IF NOT EXISTS artists
(
    artist_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    artist_name VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS albums
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    album_name   VARCHAR(255) NOT NULL,
    album_type   VARCHAR(50),
    release_date DATE,
    release_year SMALLINT,
    total_songs  INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_album_release_year ON albums(release_year);

CREATE TABLE IF NOT EXISTS album_artist_mappings
(
    album_id   BIGINT NOT NULL,
    artist_id  BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (album_id, artist_id)
);

CREATE INDEX IF NOT EXISTS idx_mapping_artist ON album_artist_mappings(artist_id);


CREATE TABLE IF NOT EXISTS songs
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    album_id         BIGINT       NOT NULL,
    song_title       VARCHAR(255),
    danceability     DECIMAL(8, 4),
    energy           DECIMAL(8, 4),
    positiveness     DECIMAL(8, 4),
    liveness         DECIMAL(8, 4),
    instrumentalness DECIMAL(10, 5),
    popularity       TINYINT
);

CREATE INDEX IF NOT EXISTS idx_song_album ON songs(album_id);

CREATE TABLE IF NOT EXISTS event_logs (
    ulid VARCHAR(26) PRIMARY KEY,
    type VARCHAR(20) NOT NULL DEFAULT 'LIKE',
    user_id VARCHAR(50),
    song_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);
CREATE INDEX IF NOT EXISTS idx_event_logs_song_id ON event_logs(song_id);
CREATE INDEX IF NOT EXISTS idx_event_logs_created_at ON event_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_event_logs_user_song ON event_logs(user_id, song_id);
