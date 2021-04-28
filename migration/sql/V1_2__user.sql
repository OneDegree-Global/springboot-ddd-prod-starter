

CREATE TABLE "user"
  (
    id varchar(100) NOT NULL,
    email varchar(100) NOT NULL UNIQUE,
    password varchar(100) NOT NULL,
    PRIMARY KEY (id)
  );