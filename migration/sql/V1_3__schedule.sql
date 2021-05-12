CREATE TABLE "schedule"
(
    name varchar(100) NOT NULL UNIQUE,
    command varchar(100) NOT NULL,
    cronExpression varchar(100) NOT NULL,
    isActive  BOOLEAN DEFAULT TRUE,
    isOverwrite BOOLEAN DEFAULT FALSE,
    effectiveTime timestamp,
    args varchar(500) DEFAULT '',
    PRIMARY KEY (name)
);