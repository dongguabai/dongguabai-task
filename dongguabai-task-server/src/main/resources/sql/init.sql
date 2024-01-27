CREATE TABLE `dongguabai_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
`execute_ms` bigint(20) NOT NULL DEFAULT '0',
  `server_seq` tinyint(4) NOT NULL DEFAULT '-1' ,
  `server_node_path` varchar(255) NOT NULL DEFAULT '0',
  `group_name` varchar(255) NOT NULL DEFAULT '0',
  `task_name` varchar(255) NOT NULL DEFAULT '0',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '处理状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job';