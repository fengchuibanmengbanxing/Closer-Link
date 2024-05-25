package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Entitys.LinkNetworkStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 清醒
 * @Date 2024/5/24 20:58
 */
@Mapper
public interface LinkNetworkStatsMapper {
    @Insert("INSERT INTO " +
            "t_link_network_stats (gid,full_short_url,date,cnt,network,create_time,update_time,del_flag) " +
            "VALUES(#{linkNetworkStats.gid}, #{linkNetworkStats.fullShortUrl}, #{linkNetworkStats.date}, #{linkNetworkStats.cnt},  #{linkNetworkStats.network}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkNetworkStats.cnt}")
    void shortLinkStatsIp(@Param("linkNetworkStats") LinkNetworkStatsDO linkNetworkStatsDO);

}
