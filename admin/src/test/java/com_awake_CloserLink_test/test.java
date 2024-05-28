package com_awake_CloserLink_test;

/**
 * @Author 清醒
 * @Date 2024/5/15 21:32
 */
public class test {
    public static final String sql = "CREATE TABLE `t_link_stats_today_%d` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
            "  `gid` varchar(32) DEFAULT NULL COMMENT '分组标识',\n" +
            "  `full_short_url` varchar(128) DEFAULT NULL COMMENT '短链接',\n" +
            "  `date` date DEFAULT NULL COMMENT '日期',\n" +
            "  `today_pv` int(11) DEFAULT NULL COMMENT 'PV',\n" +
            "  `today_uv` int(11) DEFAULT NULL COMMENT 'UV',\n" +
            "  `today_ip_count` int(11) DEFAULT NULL COMMENT 'IP',\n" +
            "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
            "  `update_time` datetime DEFAULT NULL COMMENT '修改时间',\n" +
            "  `del_flag` tinyint(4) DEFAULT NULL COMMENT '删除标识 0 未删除 1已删除',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_UNIQUE_full_short_url` (`full_short_url`,`gid`,`date`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4\n";

    public static void main(String[] args) {
        for(int i=0;i<16;i++){
            System.out.printf((sql)+"%n",i);
        }

    }


}
