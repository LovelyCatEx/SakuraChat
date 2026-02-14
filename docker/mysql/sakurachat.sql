SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

CREATE DATABASE IF NOT EXISTS `sakurachat` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sakurachat`;

DROP TABLE IF EXISTS `agents`;
CREATE TABLE IF NOT EXISTS `agents` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `prompt` text NOT NULL,
  `delimiter` varchar(16) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `chat_model_id` bigint NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `agent_channel_relations`;
CREATE TABLE IF NOT EXISTS `agent_channel_relations` (
  `channel_id` bigint NOT NULL,
  `agent_id` bigint NOT NULL,
  PRIMARY KEY (`channel_id`,`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `agent_third_party_account_relations`;
CREATE TABLE IF NOT EXISTS `agent_third_party_account_relations` (
  `agent_id` bigint NOT NULL,
  `third_party_account_id` bigint NOT NULL,
  PRIMARY KEY (`third_party_account_id`,`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `chat_models`;
CREATE TABLE IF NOT EXISTS `chat_models` (
  `id` bigint NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `provider_id` bigint NOT NULL,
  `qualified_name` varchar(256) NOT NULL,
  `max_context_tokens` int NOT NULL DEFAULT '-1',
  `temperature` int NOT NULL DEFAULT '-1',
  `max_tokens` int NOT NULL DEFAULT '-1',
  `input_token_point_rate` int NOT NULL DEFAULT '10000' COMMENT '10000:1',
  `output_token_point_rate` int NOT NULL DEFAULT '10000' COMMENT '10000:1',
  `cached_input_token_point_rate` int NOT NULL DEFAULT '10000' COMMENT '10000:1',
  `credential_id` bigint NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `credentials`;
CREATE TABLE IF NOT EXISTS `credentials` (
  `id` bigint NOT NULL,
  `type` int NOT NULL,
  `data` text NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `im_channels`;
CREATE TABLE IF NOT EXISTS `im_channels` (
  `id` bigint NOT NULL,
  `channel_name` varchar(64) NOT NULL,
  `channel_type` int NOT NULL DEFAULT '0',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `im_channel_messages`;
CREATE TABLE IF NOT EXISTS `im_channel_messages` (
  `id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `member_type` int NOT NULL,
  `member_id` bigint NOT NULL,
  `platform` int NOT NULL,
  `content` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `lark_bot_applications`;
CREATE TABLE IF NOT EXISTS `lark_bot_applications` (
  `id` bigint NOT NULL,
  `name` varchar(64) NOT NULL,
  `owner_user_id` bigint NOT NULL,
  `app_id` varchar(256) NOT NULL,
  `app_secret` varchar(256) NOT NULL,
  `encrypt_key` varchar(256) NOT NULL,
  `verification_token` varchar(256) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `napcat_group_messages`;
CREATE TABLE IF NOT EXISTS `napcat_group_messages` (
  `id` bigint NOT NULL,
  `message_id` int NOT NULL,
  `group_id` bigint NOT NULL,
  `bot_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `sender_nickname` varchar(64) CHARACTER SET utf8mb3 NOT NULL,
  `message` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL,
  `hash` varchar(64) CHARACTER SET utf8mb3 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `napcat_private_messages`;
CREATE TABLE IF NOT EXISTS `napcat_private_messages` (
  `id` bigint NOT NULL,
  `message_id` int NOT NULL,
  `bot_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `sender_nickname` varchar(64) CHARACTER SET utf8mb3 NOT NULL,
  `message` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `points_cdkeys`;
CREATE TABLE IF NOT EXISTS `points_cdkeys` (
  `id` bigint NOT NULL,
  `cdkey` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `points` bigint NOT NULL,
  `generated_by` bigint NOT NULL COMMENT 'user_id',
  `used_by` bigint DEFAULT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `points_cdkeys_pk_2` (`cdkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `providers`;
CREATE TABLE IF NOT EXISTS `providers` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `icon` blob,
  `api_type` int NOT NULL,
  `chat_completions_url` varchar(1024) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `system_settings`;
CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` bigint NOT NULL,
  `config_key` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `system_settings_pk_2` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `third_party_accounts`;
CREATE TABLE IF NOT EXISTS `third_party_accounts` (
  `id` bigint NOT NULL,
  `account_id` varchar(64) NOT NULL,
  `nickname` varchar(256) NOT NULL,
  `platform` int NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `third_party_accounts_unique` (`account_id`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `third_party_groups`;
CREATE TABLE IF NOT EXISTS `third_party_groups` (
  `id` bigint NOT NULL,
  `platform` int NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `third_party_group_channel_relations`;
CREATE TABLE IF NOT EXISTS `third_party_group_channel_relations` (
  `third_party_group_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  PRIMARY KEY (`third_party_group_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `nickname` varchar(64) NOT NULL,
  `email` varchar(256) NOT NULL,
  `points` bigint NOT NULL DEFAULT '0',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `user_channel_relations`;
CREATE TABLE IF NOT EXISTS `user_channel_relations` (
  `channel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `user_points_logs`;
CREATE TABLE IF NOT EXISTS `user_points_logs` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `delta_points` bigint NOT NULL,
  `after_balance` bigint NOT NULL,
  `reason_type` int NOT NULL,
  `related_table_type_1` int DEFAULT NULL,
  `related_table_id_1` bigint DEFAULT NULL,
  `related_table_type_2` int DEFAULT NULL,
  `related_table_id_2` bigint DEFAULT NULL,
  `related_table_type_3` int DEFAULT NULL,
  `related_table_id_3` bigint DEFAULT NULL,
  `related_table_type_4` int DEFAULT NULL,
  `related_table_id_4` bigint DEFAULT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `user_role_relations`;
CREATE TABLE IF NOT EXISTS `user_role_relations` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

DROP TABLE IF EXISTS `user_third_party_account_relations`;
CREATE TABLE IF NOT EXISTS `user_third_party_account_relations` (
  `user_id` bigint NOT NULL,
  `third_party_account_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`third_party_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
