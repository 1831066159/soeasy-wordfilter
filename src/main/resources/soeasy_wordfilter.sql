/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.10.1_LOCAL
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost
 Source Database       : soeasy_wordfilter

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : utf-8

 Date: 04/02/2019 11:26:45 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `kw_user`
-- ----------------------------
DROP TABLE IF EXISTS `kw_user`;
CREATE TABLE `kw_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `kw_user`
-- ----------------------------
BEGIN;
INSERT INTO `kw_user` VALUES ('1', 'admin', '21232f297a57a5a743894a0e4a801fc3', '010'), ('2', 'guest', '084e0343a0486ff05530df6c705c8bb4', '020');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
