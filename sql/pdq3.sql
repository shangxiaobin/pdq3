
CREATE DATABASE IF NOT EXISTS pdq3;

use  pdq3;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `username_` varchar(55) NOT NULL,
  `password_` varchar(55) DEFAULT NULL,
  PRIMARY KEY (`username_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_catalog
-- ----------------------------
DROP TABLE IF EXISTS `user_catalog`;
CREATE TABLE `user_catalog` (
  `username` varchar(55) NOT NULL,
  `catalog_` varchar(255) NOT NULL,
  `schema_` varchar(255) NOT NULL,
  PRIMARY KEY (`username`,`catalog_`,`schema_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_sql
-- ----------------------------
DROP TABLE IF EXISTS `user_sql`;
CREATE TABLE `user_sql` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username_` varchar(55) DEFAULT NULL,
  `sql_` varchar(1023) DEFAULT NULL,
  `desc_` varchar(511) DEFAULT NULL,
  `create_time_` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

INSERT INTO `user` VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3');
INSERT INTO `user_catalog` VALUES ('admin', 'all', 'all');
