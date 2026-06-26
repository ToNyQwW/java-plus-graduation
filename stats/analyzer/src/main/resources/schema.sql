CREATE SCHEMA IF NOT EXISTS stats_analyzer;

DROP TABLE IF EXISTS stats_analyzer.event_similarities CASCADE;
DROP TABLE IF EXISTS stats_analyzer.user_actions CASCADE;

CREATE TABLE IF NOT EXISTS stats_analyzer.user_actions
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT           NOT NULL,
    event_id         BIGINT           NOT NULL,
    weight           DOUBLE PRECISION NOT NULL,
    timestamp_action TIMESTAMP        NOT NULL
);

CREATE TABLE stats_analyzer.event_similarities
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_a         BIGINT           NOT NULL,
    event_b         BIGINT           NOT NULL,
    score           DOUBLE PRECISION NOT NULL,
    timestamp_event TIMESTAMP        NOT NULL
);