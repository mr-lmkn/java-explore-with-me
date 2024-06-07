DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_to_event CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS event_likes CASCADE;

CREATE TABLE IF NOT EXISTS users(
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    name     VARCHAR(250) UNIQUE NOT NULL,
    email    VARCHAR(254) UNIQUE NOT NULL,
    is_admin BOOLEAN
);

CREATE TABLE IF NOT EXISTS categories(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS locations(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    lat  NUMERIC,
    lon  NUMERIC
);

CREATE TABLE IF NOT EXISTS events(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    title              VARCHAR(120) UNIQUE NOT NULL,
    annotation         VARCHAR(2000) NOT NULL,
    description        VARCHAR(7000),
    paid               BOOLEAN,
    category_id        BIGINT NOT NULL,
    create_date        TIMESTAMP WITHOUT TIME ZONE,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT NOT NULL,
    location_id        BIGINT,
    participant_limit  INTEGER DEFAULT 0,
    confirmed_Requests INTEGER DEFAULT 0,
    published_date     TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN DEFAULT true,
    status_id          INTEGER,
    views              BIGINT,
    CONSTRAINT fk_event_to_user
        FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_to_category
        FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_location
        FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS requests(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id     BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    create_date  TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(20),
    CONSTRAINT fk_requests_to_event
        FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_requests_to_user
        FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    pinned BOOLEAN      NOT NULL,
    title  VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_to_event(
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    CONSTRAINT fk_event_compilation_to_event
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_compilation_to_compilation
        FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    CONSTRAINT uq_event_compilation_event_id_compilation_id UNIQUE (event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS event_likes (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id       BIGINT NOT NULL,
    user_id        BIGINT NOT NULL,
    is_like        BOOLEAN,
    created_date   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_event_likes_to_event
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_likes_to_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_event_likes_event_id_user_id UNIQUE (event_id, user_id)
);

CREATE OR REPLACE  VIEW v_event_rating ( event_id, rating, likes, dislikes )
    AS SELECT l.event_id
            , sum(
                   CASE WHEN l.is_like = true  THEN 1 ELSE 0 END
                -  CASE WHEN l.is_like = false THEN 1 ELSE 0 END
              ) as rating
            , sum( CASE WHEN l.is_like = true   THEN 1 ELSE 0 END ) as likes
            , sum( CASE WHEN l.is_like = false  THEN 1 ELSE 0 END ) as dislikes
         FROM event_likes l
     GROUP BY l.event_id;