/**
  Скрипт для создания/пересоздания новых таблиц
 */

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id       VARCHAR(50) PRIMARY KEY,
    login    VARCHAR(50)  NOT NULL,
    password VARCHAR(255) NOT NULL
);
