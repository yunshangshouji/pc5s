/*
SQLyog Enterprise - MySQL GUI v7.15 
MySQL - 5.5.34 : Database - pc5s
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`pc5s` /*!40100 DEFAULT CHARACTER SET utf8 */;

/*Table structure for table `his_login` */

CREATE TABLE `his_login` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `log_date` datetime DEFAULT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `params` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

/*Data for the table `his_login` */


/*Table structure for table `user` */

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱，即用户名',
  `passwd` varchar(100) DEFAULT NULL COMMENT '网站登录密码',
  `vpn_passwd` varchar(100) DEFAULT NULL COMMENT 'vpn密码',
  `vpn_expire_date` date DEFAULT NULL,
  `vpn_default_rule` varchar(20) DEFAULT 'Y',
  `vpn_include_domain` varchar(1000) DEFAULT NULL,
  `vpn_exclude_domain` varchar(1000) DEFAULT NULL,
  `vpn_level` varchar(50) DEFAULT NULL,
  `vpn_flow_limit` int(11) DEFAULT NULL,
  `user_level` varchar(50) DEFAULT 'free' COMMENT '用户等级：free',
  `member_expire_date` date DEFAULT NULL COMMENT '会员过期时间',
  `user_name` varchar(30) DEFAULT NULL COMMENT '用户名，子域名通配符',
  `can_set_user_name` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否可以修改用户名',
  `flow_limit_1` int(11) DEFAULT '100' COMMENT '流量M(内网)',
  `flow_limit_2` int(11) DEFAULT '50' COMMENT '流量M(vpn)',
  `max_sub_domain` int(11) DEFAULT NULL COMMENT '最大域名数',
  `create_date` datetime DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  `register_flag` varchar(6) DEFAULT '0',
  `verify_code` varchar(50) DEFAULT NULL COMMENT '注册、密码重置',
  `alive_flag` varchar(6) DEFAULT '1',
  `register_ip` varchar(50) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL COMMENT '最后一次登录时间',
  `know_channel` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_index` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`email`,`passwd`,`vpn_passwd`,`vpn_expire_date`,`vpn_default_rule`,`vpn_include_domain`,`vpn_exclude_domain`,`vpn_level`,`vpn_flow_limit`,`user_level`,`member_expire_date`,`user_name`,`can_set_user_name`,`flow_limit_1`,`flow_limit_2`,`max_sub_domain`,`create_date`,`modify_date`,`register_flag`,`verify_code`,`alive_flag`,`register_ip`,`last_login_time`,`know_channel`) values (1,'123@163.com','4124bc0a9335c27f086f24ba207a4912',NULL,NULL,'Y',NULL,NULL,NULL,NULL,'FREE',NULL,'5','0',100,50,2,'2016-06-14 15:58:48',NULL,'0','860670','0',NULL,NULL,NULL);

/*Table structure for table `user_domain` */

CREATE TABLE `user_domain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `user_domain` varchar(50) DEFAULT NULL,
  `app_key` varchar(50) DEFAULT NULL,
  `web_access_pwd` varchar(50) DEFAULT NULL,
  `white_ip_list` varchar(1000) DEFAULT NULL,
  `alive_flag` varchar(2) DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain` (`user_domain`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `user_domain` */

insert  into `user_domain`(`id`,`user_id`,`user_domain`,`app_key`,`web_access_pwd`,`white_ip_list`,`alive_flag`,`create_date`,`modify_date`) values (1,1,'example','example_key',NULL,NULL,'1',NULL,NULL);

/*Table structure for table `user_domain_cache` */

CREATE TABLE `user_domain_cache` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_domain_id` int(11) NOT NULL,
  `case_sensitive` varchar(1) NOT NULL DEFAULT '1',
  `how_match` varchar(20) NOT NULL COMMENT 'equals,starts,ends',
  `location` varchar(100) NOT NULL,
  `expire_time` int(11) NOT NULL,
  `case_args` varchar(1) NOT NULL DEFAULT '1',
  `expire_unit` varchar(20) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `modify_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user_domain_cache` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
