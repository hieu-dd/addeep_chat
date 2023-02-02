--liquibase formatted sql

--changeset tantv:1

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `users`;

CREATE TABLE IF NOT EXISTS `users`
(
    `id`           INT                              NOT NULL AUTO_INCREMENT,
    `country_code` VARCHAR(7)                       NOT NULL,
    `phone_number` VARCHAR(12)                      NOT NULL,
    `email`        VARCHAR(255)                     NULL,
    `name`         VARCHAR(128)                     NOT NULL,
    `firebase_uid` VARCHAR(36)                      NOT NULL,
    `dob`          DATE                             NULL,
    `gender`       ENUM ('Male', 'Female', 'Other') NULL,
    `avatar`       TEXT                             NULL,
    `is_active`    TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_reported`  TINYINT(1)                       NOT NULL DEFAULT 0,
    `is_blocked`   TINYINT(1)                       NOT NULL DEFAULT 0,
    `preferences`  TEXT                             NOT NULL,
    `created_at`   TIMESTAMP                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT firebase_uid_unique UNIQUE (`firebase_uid`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `user_devices`
(
    `id`           INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `user_id`      INTEGER      NOT NULL,
    `type`         VARCHAR(50)  NOT NULL,
    `device_token` VARCHAR(255) NOT NULL,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    CONSTRAINT device_token_unique UNIQUE (`device_token`),
    CONSTRAINT user_type_unique UNIQUE (`user_id`, `type`)
);

CREATE UNIQUE INDEX `idx_phone_number_country_code` ON `users` (`phone_number`, `country_code`);

CREATE INDEX `idx_phone_number` ON `users` (`phone_number`);

CREATE UNIQUE INDEX `idx_email` ON `users` (`email`);

-- -----------------------------------------------------
-- Table `user_verifications`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user_verifications`;

CREATE TABLE IF NOT EXISTS `user_verifications`
(
    `id`                INT        NOT NULL AUTO_INCREMENT,
    `user_id`           INT        NOT NULL,
    `verification_code` VARCHAR(8) NOT NULL,
    `created_at`        TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`        TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `contacts`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `contacts`;

CREATE TABLE IF NOT EXISTS `contacts`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `phone`      VARCHAR(19)  NULL,
    `email`      VARCHAR(255) NULL,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `user_contact`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `user_contact`;

CREATE TABLE IF NOT EXISTS `user_contact`
(
    `id`         INT       NOT NULL AUTO_INCREMENT,
    `user_id`    INT       NOT NULL,
    `contact_id` INT       NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`contact_id`) REFERENCES contacts (`id`) ON DELETE CASCADE,
    UNIQUE KEY `my_uniq_user_id_contact_id` (`user_id`, `contact_id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `conversation`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `conversations`;

CREATE TABLE IF NOT EXISTS `conversations`
(
    `id`         INT                      NOT NULL AUTO_INCREMENT,
    `title`      VARCHAR(40)              NOT NULL,
    `type`       VARCHAR(20)              NOT NULL,
    `creator_id` INT                      NOT NULL,
    `created_at` DATETIME                 NOT NULL,
    `updated_at` DATETIME                 NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`creator_id`) REFERENCES users (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `messages`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `messages`;

CREATE TABLE IF NOT EXISTS `messages`
(
    `id`              INT                           NOT NULL AUTO_INCREMENT,
    `conversation_id` INT                           NOT NULL,
    `sender_id`       INT                           NOT NULL,
    `message_type`    VARCHAR(20)                   NOT NULL,
    `message`         VARCHAR(255)                  NOT NULL DEFAULT '',
    `created_at`      DATETIME                      NOT NULL,
    `updated_at`      DATETIME                      NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`sender_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`conversation_id`) REFERENCES conversations (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE `attachments`
(
    `id`            INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `message_id`    INTEGER      NOT NULL,
    `type`          VARCHAR(20)  NOT NULL,
    `original_name` VARCHAR(255) NOT NULL,
    `name`          VARCHAR(300) NOT NULL,
    `md5`           CHAR(32)     NOT NULL,
    `size`          INTEGER      NOT NULL,
    `created_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`message_id`) REFERENCES messages (`id`) ON DELETE CASCADE,
    CONSTRAINT conversation_file_name_unique UNIQUE (`message_id`, `name`)
);

-- -----------------------------------------------------
-- Table `participants`
-- -----------------------------------------------------

DROP TABLE IF EXISTS `participants`;

CREATE TABLE IF NOT EXISTS `participants`
(
    `id`              INT      NOT NULL AUTO_INCREMENT,
    `conversation_id` INT      NOT NULL,
    `user_id`         INT      NOT NULL,
    `created_at`      DATETIME NOT NULL,
    `updated_at`      DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`conversation_id`) REFERENCES conversations (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

SET FOREIGN_KEY_CHECKS = 1;


DROP TABLE IF EXISTS `events`;

CREATE TABLE IF NOT EXISTS `events`
(
    `id`          INT                           NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255)                  NOT NULL,
    `description` TEXT                          NOT NULL,
    `started_at`  TIMESTAMP                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `ended_at`    TIMESTAMP                     NULL,
    `apply_on`    VARCHAR(20)                   NOT NULL,
    `status`      VARCHAR(20)                   NOT NULL,
    `image_url`   TEXT                          NOT NULL,
    `url`         TEXT                          NOT NULL,
    `created_at`  TIMESTAMP                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP                     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `event_actions`;

CREATE TABLE IF NOT EXISTS `event_actions`
(
    `id`         INT                             NOT NULL AUTO_INCREMENT,
    `event_id`   INT                             NOT NULL,
    `type`       VARCHAR(20)                     NOT NULL,
    `points`     INT                             NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP                       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP                       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`event_id`) REFERENCES events (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `point_history`;

CREATE TABLE IF NOT EXISTS `point_history`
(
    `id`          INT                                           NOT NULL AUTO_INCREMENT,
    `user_id`     INT                                           NOT NULL,
    `point`       INT                                           NOT NULL,
    `action_type` VARCHAR(20)                                   NOT NULL,
    `event_id`    INT                                           NOT NULL,
    `created_at`  TIMESTAMP                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP                                     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`event_id`) REFERENCES events (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;


DROP TABLE IF EXISTS `user_point`;

CREATE TABLE IF NOT EXISTS `user_point`
(
    `user_id`        INT       NOT NULL,
    `received_point` INT       NOT NULL,
    `balance`        INT       NOT NULL,
    `created_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES users (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `sticker_pack`;

CREATE TABLE `sticker_packs`
(
    `id`                    INTEGER   NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`                  TEXT      NOT NULL,
    `publisher`             TEXT      NOT NULL,
    `tray_image_file`       TEXT      NOT NULL,
    `animated_sticker_pack` TINYINT(1) DEFAULT 0,
    `created_at`            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS `stickers`;

CREATE TABLE `stickers`
(
    `id`           INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `sticker_pack` INTEGER      NOT NULL,
    `image_file`   VARCHAR(400) NOT NULL,
    `emoji`        JSON         NOT NULL,
    `created_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`sticker_pack`) REFERENCES sticker_packs (`id`) ON DELETE  CASCADE ,
    INDEX (`sticker_pack`),
    CONSTRAINT pack_image_file_unique UNIQUE (`sticker_pack`, `image_file`)
);

DROP TABLE IF EXISTS `message_event`;

CREATE TABLE IF NOT EXISTS `message_event`
(
    `id`         INT       NOT NULL AUTO_INCREMENT,
    `event_id`   INT       NOT NULL,
    `sticker_id`  INT       NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`event_id`) REFERENCES events (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`sticker_id`) REFERENCES stickers (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

DROP TRIGGER IF EXISTS after_insert_point_history;

--changeset anhdt:2 splitStatements:false
CREATE TRIGGER after_insert_point_history
    AFTER INSERT
    ON point_history
    FOR EACH ROW
BEGIN
    INSERT INTO user_point (user_id, received_point, balance)
        (SELECT user_id, SUM(IF(point > 0, point, 0)), SUM(point)
         FROM point_history
         where user_id = NEW.user_id
         GROUP BY user_id)
        ON DUPLICATE KEY
    UPDATE received_point = VALUES(received_point), balance = VALUES(balance), updated_at = NOW();
END;
--changeset anhdt:3
ALTER TABLE `users` ADD COLUMN `addeep_id` VARCHAR(12) NULL;
ALTER TABLE `users` ADD COLUMN `allow_to_search_by_addeep_id` BOOLEAN NOT NULL DEFAULT TRUE;
CREATE UNIQUE INDEX `idx_addeep_id` ON `users` (`addeep_id`);
CREATE INDEX `idx_allow_to_search_by_addeep_id` ON `users` (`allow_to_search_by_addeep_id`);

--changeset hieutd:4
DROP TABLE IF EXISTS `gifs`;

CREATE TABLE `gifs`
(
    `id`          INTEGER      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name`        VARCHAR(255) NOT NULL,
    `description` TEXT         NOT NULL,
    `created_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FULLTEXT (`description`),
    CONSTRAINT name_unique UNIQUE (`name`)
);