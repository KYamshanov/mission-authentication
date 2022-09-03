/**
  Скрипт для создания/пересоздания новых таблиц
 */

DROP TABLE IF EXISTS auth_share;
DROP TABLE IF EXISTS auth_sessions;
DROP TABLE IF EXISTS auth_users;
DROP TYPE IF EXISTS token_status;

CREATE TYPE token_status AS ENUM ('ACTIVE', 'PAUSED', 'INVALID');

CREATE TABLE auth_users
(
    id       VARCHAR(50) PRIMARY KEY,
    login    VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255)       NOT NULL
);

CREATE TABLE auth_sessions
(
    id         VARCHAR(50) PRIMARY KEY,
    session_id VARCHAR(50)        NOT NULL,
    refresh_id VARCHAR(50) UNIQUE NOT NULL,
    user_id    VARCHAR(50)        NOT NULL,
    created_at TIMESTAMPTZ        NOT NULL,
    updated_at TIMESTAMPTZ        NOT NULL,
    expires_at TIMESTAMPTZ        NOT NULL,
    status     token_status       NOT NULL,
    info       JSON               NOT NULL,

    FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE
);

CREATE TABLE auth_share
(
    id         VARCHAR(50) PRIMARY KEY,
    user_id    VARCHAR(50)  NOT NULL,
    session_id VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    status     token_status NOT NULL,

    FOREIGN KEY (user_id) REFERENCES auth_users (id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES auth_sessions (id) ON DELETE CASCADE
)