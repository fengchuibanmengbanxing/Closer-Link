package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Entitys.LinkLocaleStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 清醒
 * @Date 2024/5/23 15:36
 */
@Mapper
public interface LinkLocaleStatsMapper {

    @Insert("INSERT INTO " +
            "t_link_locale_stats (gid,full_short_url,date,cnt,province,city,adcode,country,create_time,update_time,del_flag) " +
            "VALUES(#{linkLocaleStats.gid}, #{linkLocaleStats.fullShortUrl}, #{linkLocaleStats.date}, #{linkLocaleStats.cnt}, #{linkLocaleStats.province}, #{linkLocaleStats.city}, #{linkLocaleStats.adcode}, #{linkLocaleStats.country}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{linkLocaleStats.cnt}")
    void shortLinkStatsIp(@Param("linkLocaleStats") LinkLocaleStatsDO linkLocaleStatsDO);

}
