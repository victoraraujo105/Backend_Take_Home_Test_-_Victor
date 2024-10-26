CREATE TYPE sleep_quality AS ENUM ('BAD', 'OK', 'GOOD');

CREATE TABLE sleep_logs
(
    id         SERIAL PRIMARY KEY,
    user_id    INT           NOT NULL REFERENCES users (id),
    start_time TIMESTAMP     NOT NULL,
    end_time   TIMESTAMP     NOT NULL,
    quality    sleep_quality NOT NULL
);