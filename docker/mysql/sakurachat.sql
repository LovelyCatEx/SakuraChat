SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
--  `sakurachat`
--

CREATE DATABASE IF NOT EXISTS sakurachat
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sakurachat;

-- --------------------------------------------------------

--
--  `agents`
--

CREATE TABLE `agents` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(512) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `prompt` text NOT NULL,
  `delimiter` varchar(16) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `chat_model_id` bigint NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `agent_channel_relations`
--

CREATE TABLE `agent_channel_relations` (
  `channel_id` bigint NOT NULL,
  `agent_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `agent_third_party_account_relations`
--

CREATE TABLE `agent_third_party_account_relations` (
  `agent_id` bigint NOT NULL,
  `third_party_account_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `chat_models`
--

CREATE TABLE `chat_models` (
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
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `credentials`
--

CREATE TABLE `credentials` (
  `id` bigint NOT NULL,
  `type` int NOT NULL,
  `data` text NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `im_channels`
--

CREATE TABLE `im_channels` (
  `id` bigint NOT NULL,
  `channel_name` varchar(64) NOT NULL,
  `channel_type` int NOT NULL DEFAULT '0',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `im_channel_messages`
--

CREATE TABLE `im_channel_messages` (
  `id` bigint NOT NULL,
  `channel_id` bigint NOT NULL,
  `member_type` int NOT NULL,
  `member_id` bigint NOT NULL,
  `platform` int NOT NULL,
  `content` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
--  `lark_bot_applications`
--

CREATE TABLE `lark_bot_applications` (
  `id` bigint NOT NULL,
  `name` varchar(64) NOT NULL,
  `owner_user_id` bigint NOT NULL,
  `app_id` varchar(256) NOT NULL,
  `app_secret` varchar(256) NOT NULL,
  `encrypt_key` varchar(256) NOT NULL,
  `verification_token` varchar(256) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `napcat_group_messages`
--

CREATE TABLE `napcat_group_messages` (
  `id` bigint NOT NULL,
  `message_id` int NOT NULL,
  `group_id` bigint NOT NULL,
  `bot_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `sender_nickname` varchar(64) CHARACTER SET utf8mb3 NOT NULL,
  `message` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL,
  `hash` varchar(64) CHARACTER SET utf8mb3 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
--  `napcat_private_messages`
--

CREATE TABLE `napcat_private_messages` (
  `id` bigint NOT NULL,
  `message_id` int NOT NULL,
  `bot_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `sender_nickname` varchar(64) CHARACTER SET utf8mb3 NOT NULL,
  `message` text CHARACTER SET utf8mb3 NOT NULL,
  `created_time` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
--  `providers`
--

CREATE TABLE `providers` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(512) DEFAULT NULL,
  `icon` blob,
  `api_type` int NOT NULL,
  `chat_completions_url` varchar(1024) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `system_settings`
--

CREATE TABLE `system_settings` (
  `id` bigint NOT NULL,
  `config_key` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
--  `third_party_accounts`
--

CREATE TABLE `third_party_accounts` (
  `id` bigint NOT NULL,
  `account_id` varchar(64) NOT NULL,
  `nickname` varchar(256) NOT NULL,
  `platform` int NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `third_party_groups`
--

CREATE TABLE `third_party_groups` (
  `id` bigint NOT NULL,
  `platform` int NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `name` varchar(128) NOT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `third_party_group_channel_relations`
--

CREATE TABLE `third_party_group_channel_relations` (
  `third_party_group_id` bigint NOT NULL,
  `channel_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `users`
--

CREATE TABLE `users` (
  `id` bigint NOT NULL,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `nickname` varchar(64) NOT NULL,
  `email` varchar(256) NOT NULL,
  `points` bigint NOT NULL DEFAULT '0',
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `user_channel_relations`
--

CREATE TABLE `user_channel_relations` (
  `channel_id` bigint NOT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `user_points_logs`
--

CREATE TABLE `user_points_logs` (
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
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
--  `user_roles`
--

CREATE TABLE `user_roles` (
  `id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `created_time` bigint NOT NULL,
  `modified_time` bigint NOT NULL,
  `deleted_time` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `user_role_relations`
--

CREATE TABLE `user_role_relations` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
--  `user_third_party_account_relations`
--

CREATE TABLE `user_third_party_account_relations` (
  `user_id` bigint NOT NULL,
  `third_party_account_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
--  `agents`
--
ALTER TABLE `agents`
  ADD PRIMARY KEY (`id`);

--
--  `agent_channel_relations`
--
ALTER TABLE `agent_channel_relations`
  ADD PRIMARY KEY (`channel_id`,`agent_id`);

--
--  `agent_third_party_account_relations`
--
ALTER TABLE `agent_third_party_account_relations`
  ADD PRIMARY KEY (`third_party_account_id`,`agent_id`);

--
--  `chat_models`
--
ALTER TABLE `chat_models`
  ADD PRIMARY KEY (`id`);

--
--  `credentials`
--
ALTER TABLE `credentials`
  ADD PRIMARY KEY (`id`);

--
--  `im_channels`
--
ALTER TABLE `im_channels`
  ADD PRIMARY KEY (`id`);

--
--  `im_channel_messages`
--
ALTER TABLE `im_channel_messages`
  ADD PRIMARY KEY (`id`);

--
--  `lark_bot_applications`
--
ALTER TABLE `lark_bot_applications`
  ADD PRIMARY KEY (`id`);

--
--  `napcat_group_messages`
--
ALTER TABLE `napcat_group_messages`
  ADD PRIMARY KEY (`id`);

--
--  `napcat_private_messages`
--
ALTER TABLE `napcat_private_messages`
  ADD PRIMARY KEY (`id`);

--
--  `providers`
--
ALTER TABLE `providers`
  ADD PRIMARY KEY (`id`);

--
--  `system_settings`
--
ALTER TABLE `system_settings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `system_settings_pk_2` (`config_key`);

--
--  `third_party_accounts`
--
ALTER TABLE `third_party_accounts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `third_party_accounts_unique` (`account_id`,`platform`);

--
--  `third_party_group_channel_relations`
--
ALTER TABLE `third_party_group_channel_relations`
  ADD PRIMARY KEY (`third_party_group_id`,`channel_id`);

--
--  `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
--  `user_channel_relations`
--
ALTER TABLE `user_channel_relations`
  ADD PRIMARY KEY (`user_id`,`channel_id`);

--
--  `user_points_logs`
--
ALTER TABLE `user_points_logs`
  ADD PRIMARY KEY (`id`);

--
--  `user_roles`
--
ALTER TABLE `user_roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
--  `user_role_relations`
--
ALTER TABLE `user_role_relations`
  ADD PRIMARY KEY (`id`);

--
--  `user_third_party_account_relations`
--
ALTER TABLE `user_third_party_account_relations`
  ADD PRIMARY KEY (`user_id`,`third_party_account_id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
