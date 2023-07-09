SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS=0;

## all of your schema and inserts

CREATE TABLE IF NOT EXISTS `user` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `email` varchar(255) DEFAULT NULL,
                                      `first_name` varchar(255) DEFAULT NULL,
                                      `last_name` varchar(255) DEFAULT NULL,
                                      `password` varchar(255) DEFAULT NULL,
                                      `phone_number` varchar(255) DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `event` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `description` varchar(255) DEFAULT NULL,
                         `end_date` datetime(6) DEFAULT NULL,
                         `event_name` varchar(255) DEFAULT NULL,
                         `location` varchar(255) DEFAULT NULL,
                         `start_date` datetime(6) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `event_user` (
                              `event_id` bigint NOT NULL,
                              `user_id` bigint NOT NULL,
                              PRIMARY KEY (`event_id`,`user_id`),
                              KEY `FK67g39uhr99s8ney3d8tccqtf6` (`user_id`),
                              CONSTRAINT `FK67g39uhr99s8ney3d8tccqtf6` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                              CONSTRAINT `FKtc58o1e7bpugjcxuqr8l05l12` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE IF NOT EXISTS `user_role` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UK_qgifji1sgimaktdeyedicoa45` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `user_roles` (
                              `user_id` bigint NOT NULL,
                              `role_id` bigint NOT NULL,
                              KEY `FK398mx7nkmwtrdd9923ji27ovh` (`role_id`),
                              KEY `FK55itppkw3i07do3h7qoclqd4k` (`user_id`),
                              CONSTRAINT `FK398mx7nkmwtrdd9923ji27ovh` FOREIGN KEY (`role_id`) REFERENCES `user_role` (`id`),
                              CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;