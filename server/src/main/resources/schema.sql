-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
-- Host: localhost    Database: screen_vault
-- ------------------------------------------------------
-- Server version   8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

USE `screen_vault`;

DROP TABLE IF EXISTS `tag_content`;
DROP TABLE IF EXISTS `list_content`;
DROP TABLE IF EXISTS `user_content`;
DROP TABLE IF EXISTS `user_preference`;
DROP TABLE IF EXISTS `tags`;
DROP TABLE IF EXISTS `lists`;
DROP TABLE IF EXISTS `contents`;
DROP TABLE IF EXISTS `users`;

-- 1. 主表：users
CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `email` varchar(255) DEFAULT NULL,
                         `password` varchar(255) DEFAULT NULL,
                         `username` varchar(255) DEFAULT NULL,
                         `enabled` bit(1) DEFAULT NULL,
                         `created_at` datetime(6) DEFAULT NULL,
                         `nickname` varchar(50) DEFAULT NULL,
                         `provider` varchar(255) DEFAULT NULL,
                         `provider_id` varchar(255) DEFAULT NULL,
                         `updated_at` datetime(6) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. 主表：contents (依赖 users)
CREATE TABLE `contents` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `category` enum('MOVIE','TV_SHOW') DEFAULT NULL,
                            `country` varchar(2) DEFAULT NULL,
                            `description` text,
                            `genre` enum(
    'ACTION','ACTION_ADVENTURE','ADVENTURE','ANIMATION','COMEDY','CRIME',
    'DOCUMENTARY','DRAMA','FAMILY','FANTASY','HISTORY','HORROR','KIDS',
    'MUSIC','MYSTERY','NEWS','REALITY','ROMANCE','SCIENCE_FICTION',
    'SCI_FI_FANTASY','SOAP','TALK','THRILLER','TV_MOVIE',
    'WAR','WAR_POLITICS','WESTERN'
  ) DEFAULT NULL,
                            `image` varchar(500) DEFAULT NULL,
                            `language` varchar(2) DEFAULT NULL,
                            `other_title` varchar(500) DEFAULT NULL,
                            `release_date` date DEFAULT NULL,
                            `source_type` enum('CUSTOM_DATA','OFFICIAL_DATA') DEFAULT NULL,
                            `title` varchar(500) NOT NULL,
                            `creator_id` int DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `idx_contents_creator` (`creator_id`),
                            CONSTRAINT `fk_contents_user` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1298927 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. 主表：lists (依赖 users)
CREATE TABLE `lists` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `create_date` date DEFAULT NULL,
                         `is_default` bit(1) DEFAULT NULL,
                         `list_name` varchar(225) DEFAULT NULL,
                         `creator_id` int NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `idx_lists_creator` (`creator_id`),
                         CONSTRAINT `fk_lists_user` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. 主表：tags (依赖 users)
CREATE TABLE `tags` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `tag_name` varchar(50) DEFAULT NULL,
                        `creator_id` int NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `idx_tags_creator` (`creator_id`),
                        CONSTRAINT `fk_tags_user` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. 主表：user_preference (依赖 users)
CREATE TABLE `user_preference` (
                                   `user_id` int NOT NULL,
                                   `bg_image` varchar(255) DEFAULT NULL,
                                   `layout` json DEFAULT NULL,
                                   `theme` varchar(255) DEFAULT NULL,
                                   `update_time` datetime(6) DEFAULT NULL,
                                   PRIMARY KEY (`user_id`),
                                   CONSTRAINT `fk_userpref_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. 子表：user_content (依赖 users, contents)
CREATE TABLE `user_content` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `rate` decimal(3,1) DEFAULT NULL,
                                `review` json DEFAULT NULL,
                                `status` enum('WANT_TO_WATCH','WATCHED') DEFAULT NULL,
                                `watch_date` date DEFAULT NULL,
                                `content_id` int NOT NULL,
                                `user_id` int NOT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_user_content` (`user_id`,`content_id`),
                                KEY `idx_uc_content` (`content_id`),
                                CONSTRAINT `fk_uc_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                                CONSTRAINT `fk_uc_content` FOREIGN KEY (`content_id`) REFERENCES `contents` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7. 子表：list_content (依赖 contents, lists)
CREATE TABLE `list_content` (
                                `add_time` datetime(6) DEFAULT NULL,
                                `content_id` int NOT NULL,
                                `list_id` int NOT NULL,
                                PRIMARY KEY (`content_id`,`list_id`),
                                KEY `idx_lc_list` (`list_id`),
                                CONSTRAINT `fk_lc_content` FOREIGN KEY (`content_id`) REFERENCES `contents` (`id`),
                                CONSTRAINT `fk_lc_list` FOREIGN KEY (`list_id`) REFERENCES `lists` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8. 子表：tag_content (依赖 contents, tags)
CREATE TABLE `tag_content` (
                               `content_id` int NOT NULL,
                               `tag_id` int NOT NULL,
                               PRIMARY KEY (`content_id`,`tag_id`),
                               KEY `idx_tc_tag` (`tag_id`),
                               CONSTRAINT `fk_tc_content` FOREIGN KEY (`content_id`) REFERENCES `contents` (`id`),
                               CONSTRAINT `fk_tc_tag` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */; 