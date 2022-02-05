CREATE TABLE IF NOT EXISTS administrators
(
  administrator_id       integer      NOT NULL UNIQUE PRIMARY KEY,
  administrator_login    varchar(30)  NOT NULL UNIQUE,
  administrator_password varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
  user_id       integer     NOT NULL UNIQUE PRIMARY KEY,
  user_login    varchar(30) NOT NULL UNIQUE,
  user_name     varchar(30) NOT NULL,
  user_birthday DATE
);