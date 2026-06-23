DROP TABLE IF EXISTS comments;

CREATE TABLE IF NOT EXISTS comments
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text       VARCHAR(2000)               NOT NULL,
    event_id   BIGINT                      NOT NULL,
    author_id  BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status     VARCHAR(100)                NOT NULL
);