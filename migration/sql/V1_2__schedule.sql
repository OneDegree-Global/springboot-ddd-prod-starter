CREATE TABLE "user"
(
    name varchar(100) NOT NULL UNIQUE,
    command varchar(100) NOT NULL,
    cronExpression varchar(100) NOT NULL,
    isActive  BIT DEFAULT 1,
    isOverwrite BIT DEFAULT 0,
    effectiveTime timestamp,
    args varchar(500) DEFAULT '',
    PRIMARY KEY (name),
);