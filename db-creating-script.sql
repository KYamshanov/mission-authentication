/**
  Скрипт для создания/пересоздания новых таблиц
 */

DROP TABLE IF EXISTS auth_session_tokens;
DROP TABLE IF EXISTS auth_share;
DROP TABLE IF EXISTS auth_sessions;
DROP TABLE IF EXISTS auth_users;

DROP TYPE IF EXISTS entity_status;

DROP INDEX IF EXISTS auth_session_tokens_refresh_id;
DROP INDEX IF EXISTS auth_sessions_user_id;

CREATE TYPE entity_status AS ENUM ('ACTIVE', 'PAUSED', 'BLOCKED', 'INVALID');

CREATE TABLE auth_users
(
    id       VARCHAR(50) PRIMARY KEY,
    login    VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255)       NOT NULL
);

CREATE TABLE auth_sessions
(
    id         VARCHAR(50) PRIMARY KEY,
    user_id    VARCHAR(50)   NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL,
    updated_at TIMESTAMPTZ   NOT NULL,
    status     entity_status NOT NULL,

    FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE TABLE auth_session_tokens
(
    id         VARCHAR(50) PRIMARY KEY,
    refresh_id VARCHAR(50) UNIQUE NOT NULL,
    session_id VARCHAR(50)        NOT NULL,
    created_at TIMESTAMPTZ        NOT NULL,
    updated_at TIMESTAMPTZ        NOT NULL,
    expires_at TIMESTAMPTZ        NOT NULL,
    status     entity_status      NOT NULL,
    info       JSON               NOT NULL,

    FOREIGN KEY (session_id) REFERENCES auth_sessions (id) ON DELETE CASCADE
);

CREATE TABLE auth_share
(
    id         VARCHAR(50) PRIMARY KEY,
    user_id    VARCHAR(50)   NOT NULL,
    session_id VARCHAR(50)   NOT NULL,
    created_at TIMESTAMPTZ   NOT NULL,
    expires_at TIMESTAMPTZ   NOT NULL,
    status     entity_status NOT NULL,

    FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES auth_sessions (id) ON DELETE CASCADE
);

CREATE INDEX auth_session_tokens_refresh_id ON auth_session_tokens (refresh_id);
CREATE INDEX auth_sessions_user_id ON auth_sessions (user_id);