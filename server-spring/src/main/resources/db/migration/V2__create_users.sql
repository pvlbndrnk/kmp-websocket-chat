CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    username VARCHAR(32) NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX users_lower_username_idx ON users(lower(username));