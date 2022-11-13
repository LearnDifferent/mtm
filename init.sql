CREATE DATABASE IF NOT EXISTS mtm;

USE mtm;

CREATE TABLE IF NOT EXISTS `user`
(
    `id`            varchar(255) NOT NULL,
    `user_name`     varchar(50)  NOT NULL,
    `password`      varchar(255) NOT NULL,
    `creation_time` datetime    DEFAULT NULL,
    `role`          varchar(10) DEFAULT 'guest',
    `is_deleted`    boolean     DEFAULT false,
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_name_unique` (`user_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `bookmark`
(
    `id`            int(11) unsigned NOT NULL AUTO_INCREMENT,
    `user_name`     varchar(50)      NOT NULL,
    `title`         varchar(255) DEFAULT NULL,
    `url`           varchar(600)     NOT NULL,
    `img`           varchar(600) DEFAULT NULL,
    `desc`          text,
    `creation_time` datetime     DEFAULT NULL,
    `is_public`     boolean      DEFAULT true,
    `is_deleted`    boolean      DEFAULT false,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `comment`
(
    `id`                  int(11) unsigned NOT NULL AUTO_INCREMENT,
    `comment`             varchar(140)     NOT NULL,
    `bookmark_id`         int(11)          NOT NULL,
    `username`            varchar(50)      NOT NULL,
    `creation_time`       datetime         NOT NULL,
    `reply_to_comment_id` int(11) DEFAULT NULL,
    `is_deleted`          boolean DEFAULT false,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `comment_history`
(
    `comment_id`    int(11) unsigned NOT NULL,
    `comment`       varchar(140)     NOT NULL,
    `creation_time` datetime         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `system_log`
(
    `title`    varchar(256)  NOT NULL,
    `opt_type` varchar(10)   NOT NULL,
    `method`   varchar(128)  NOT NULL,
    `msg`      varchar(1024) NOT NULL,
    `status`   varchar(10)   NOT NULL,
    `opt_time` datetime      NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `bookmark_view`
(
    `bookmark_id` int(11) not null,
    `views`       int(11) not null
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `tag`
(
    `tag`         varchar(1024) not null,
    `bookmark_id` int(11)       not null,
    `is_deleted`  boolean DEFAULT false
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


insert into user
values ('first_user', 'Guest', '0bb2b8178920142d4598bd4b61924a2c', CURRENT_DATE(), 'guest');