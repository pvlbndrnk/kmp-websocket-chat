CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    username VARCHAR(32) NOT NULL UNIQUE CHECK(username = lower(username)),
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);