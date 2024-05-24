package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Entitys.LinkOSStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 清醒
 * @Date 2024/5/24 15:33
 */
@Mapper
public interface LinkOSStatsMapper {

    @Insert("INSERT INTO " +
            "t_link_os_stats (gid,full_short_url,date,cnt,os,create_time,update_time,del_flag) " +
            "VALUES(#{linkOSStats.gid}, #{linkOSStats.fullShortUrl}, #{linkOSStats.date}, #{linkOSStats.cnt}, #{linkOSStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkOSStats.cnt}")
    void shortLinkStatsOS(@Param("linkOSStats") LinkOSStatsDO linkOSStatsDO);

}
