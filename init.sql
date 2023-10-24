CREATE DATABASE IF NOT EXISTS mtm;

USE mtm;

CREATE TABLE IF NOT EXISTS `user`
(
    `id`            int(11) unsigned NOT NULL AUTO_INCREMENT,
    `user_name`     varchar(50)      NOT NULL,
    `password`      varchar(255)     NOT NULL,
    `creation_time` datetime    DEFAULT NULL,
    `role`          varchar(10) DEFAULT 'guest',
    `is_deleted`    boolean     DEFAULT false,
    PRIMARY KEY (`id`),
    UNIQUE KEY `user_name_unique` (`user_name`),
    KEY `idx_user_name_password_creation_time_role` (`user_name`, `password`, `creation_time`, `role`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `bookmark`
(
    `id`            bigint unsigned NOT NULL,
    `user_id`       bigint unsigned NOT NULL,
    `title`         varchar(255) DEFAULT NULL,
    `url`           varchar(600)    NOT NULL,
    `img`           varchar(600) DEFAULT NULL,
    `desc`          text,
    `creation_time` datetime     DEFAULT NULL,
    `is_public`     boolean      DEFAULT true,
    `is_deleted`    boolean      DEFAULT false,
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_idx_user_id_url` (`user_id`, `url`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `comment`
(
    `id`                  bigint unsigned NOT NULL AUTO_INCREMENT,
    `comment`             varchar(140)    NOT NULL,
    `bookmark_id`         bigint unsigned NOT NULL,
    `user_id`             bigint unsigned NOT NULL,
    `creation_time`       datetime        NOT NULL,
    `reply_to_comment_id` bigint          NOT NULL DEFAULT -1,
    `is_deleted`          tinyint(1)               DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_comment_bookmark_id_user_id` (`bookmark_id`, `user_id`),
    KEY `idx_comment_reply_to_comment_id_bookmark_id_creation_time` (`reply_to_comment_id`, `bookmark_id`, `creation_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `comment_history`
(
    `comment_id`    bigint unsigned NOT NULL,
    `comment`       varchar(140)    NOT NULL,
    `creation_time` datetime        NOT NULL,
    KEY `idx_comment_history_id_time` (`comment_id`, `creation_time`)
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
    `views`       int(11) not null,
    /*
        当 SQL 为：
    ```sql
    select
        b.user_name, b.title, b.url, b.is_public, v.bookmark_id, v.views
    from bookmark_view v
    left join bookmark b
      on v.bookmark_id = b.id
    order by v.views desc;
    ```
        的时候，使用 `views`, `bookmark_id` 的联合索引，
        可以达到 Backward index scan; Using index
     */

    KEY `idx_bookmark_view_views_bookmark_id` (`views`, `bookmark_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `tag`
(
    `tag`         varchar(8) not null,
    `bookmark_id` int(11)    not null,
    UNIQUE KEY `idx_tag_tag_bookmark_id` (`tag`, `bookmark_id`),
    KEY `idx_tag_bookmark_id` (`bookmark_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `id_generator`
(
    `biz_tag`     varchar(256) NOT NULL,
    `max_id`      bigint       NOT NULL DEFAULT '1',
    `step`        int          NOT NULL DEFAULT '100',
    `description` varchar(256)          DEFAULT NULL,
    `update_time` timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`biz_tag`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `reply_notification`
(
    `id`                  bigint unsigned NOT NULL,
    `message`             varchar(600),
    `creation_time`       timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `sender_user_id`      bigint unsigned NOT NULL,
    `recipient_user_id`   bigint unsigned NOT NULL,
    `comment_id`          bigint unsigned NOT NULL,
    `bookmark_id`         bigint unsigned NOT NULL,
    `reply_to_comment_id` bigint unsigned,
    `is_read`             boolean                  DEFAULT false,
    `update_time`         timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `user_system_notification`
(
    `notification_id`   bigint unsigned NOT NULL,
    `recipient_user_id` bigint unsigned NOT NULL,
    `message`           varchar(600),
    `creation_time`     timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `sender`            varchar(256)    NOT NULL,
    `is_read`           boolean                  DEFAULT false,
    `update_time`       timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- the primary key is notification ID and user ID
    PRIMARY KEY (`notification_id`, `recipient_user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS `system_notification`
(
    `id`            bigint unsigned NOT NULL,
    `message`       varchar(600),
    `creation_time` timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `sender`        varchar(256)    NOT NULL,
    `update_time`   timestamp       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

insert into user(user_name, password, creation_time, role)
values ('Guest', '0bb2b8178920142d4598bd4b61924a2c', CURRENT_DATE(), 'guest');