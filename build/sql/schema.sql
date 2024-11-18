-- init exchange database

SET NAMES utf8mb4;
DROP DATABASE IF EXISTS exchangedb;
CREATE DATABASE exchangedb;
USE exchangedb;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
                             `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
                             `username` VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
                             `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
                             `email` VARCHAR(100) DEFAULT NULL COMMENT '用户邮箱（可选）',
                             `phone` VARCHAR(20) DEFAULT NULL COMMENT '用户手机号（可选）',
                             `last_login` DATETIME DEFAULT NULL COMMENT '最近登录时间',
                             `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '软删除标识：0=未删除，1=已删除',
                             `version` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详情表';

-- ----------------------------
-- Table structure for asset_info
-- ----------------------------
DROP TABLE IF EXISTS `asset_info`;
CREATE TABLE `asset_info` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资产记录唯一标识',
                              `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户唯一标识',
                              `asset_type_id` BIGINT UNSIGNED NOT NULL COMMENT '资产类型标识',
                              `available_balance` DECIMAL(36,18) NOT NULL DEFAULT '0.000000000000000000' COMMENT '可用余额',
                              `frozen_balance` DECIMAL(36,18) NOT NULL DEFAULT '0.000000000000000000' COMMENT '冻结余额',
                              `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '软删除标识：0=未删除，1=已删除',
                              `version` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `idx_user_asset` (`user_id`, `asset_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产详情表';


-- ----------------------------
-- Table structure for asset_transaction
-- ----------------------------
DROP TABLE IF EXISTS `asset_transaction`;
CREATE TABLE `asset_transaction` (
                                     `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '交易记录唯一标识',
                                     `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户唯一标识',
                                     `asset_type_id` BIGINT UNSIGNED NOT NULL COMMENT '资产类型标识',
                                     `transaction_type` TINYINT UNSIGNED NOT NULL COMMENT '交易类型：1=充值，2=提现，3=冻结，4=解冻',
                                     `amount` DECIMAL(36,18) NOT NULL COMMENT '交易金额（正为增加，负为减少）',
                                     `available_balance_after` DECIMAL(36,18) NOT NULL COMMENT '交易后的可用余额',
                                     `frozen_balance_after` DECIMAL(36,18) NOT NULL COMMENT '交易后的冻结余额',
                                     `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '软删除标识：0=未删除，1=已删除',
                                     `version` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                                     `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
                                     `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_user_transaction` (`user_id`, `asset_type_id`),
                                     KEY `idx_transaction_type` (`transaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产交易记录表';

-- ----------------------------
-- Table structure for asset_type
-- ----------------------------
DROP TABLE IF EXISTS `asset_type`;
CREATE TABLE `asset_type` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '资产类型唯一标识',
                              `asset_name` VARCHAR(50) NOT NULL COMMENT '资产名称（如 Bitcoin）',
                              `asset_symbol` VARCHAR(20) NOT NULL COMMENT '资产符号（如 BTC）',
                              `asset_precision` TINYINT UNSIGNED NOT NULL DEFAULT '18' COMMENT '资产小数精度',
                              `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '软删除标识：0=未删除，1=已删除',
                              `version` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `idx_asset_name` (`asset_name`),
                              UNIQUE KEY `idx_asset_symbol` (`asset_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产类型表';


-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
                              `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单唯一标识',
                              `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户唯一标识',
                              `sequence_id` BIGINT UNSIGNED NOT NULL COMMENT '定序ID',
                              `order_direction` TINYINT UNSIGNED NOT NULL COMMENT '订单方向：0=买，1=卖',
                              `order_price` DECIMAL(18,8) NOT NULL COMMENT '订单价格',
                              `order_quantity` DECIMAL(18,8) NOT NULL COMMENT '订单总数量',
                              `unfilled_quantity` DECIMAL(18,8) NOT NULL COMMENT '未成交数量',
                              `order_status` TINYINT UNSIGNED NOT NULL COMMENT '订单状态：0=等待，1=部分成交，2=完全成交，3=已取消',
                              `is_deleted` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '软删除标识：0=未删除，1=已删除',
                              `version` BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_order_status` (`order_status`),
                              KEY `idx_sequence_id` (`sequence_id`),
                              KEY `idx_order_direction` (`order_direction`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

