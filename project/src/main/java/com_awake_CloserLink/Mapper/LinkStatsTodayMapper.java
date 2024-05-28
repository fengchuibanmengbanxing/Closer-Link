package com_awake_CloserLink.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com_awake_CloserLink.Entitys.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 清醒
 * @Date 2024/5/27 20:51
 */
@Mapper
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {

    @Insert("INSERT INTO " +
            "t_link_stats_today (gid,full_short_url,date,today_pv,today_uv,today_ip_count,create_time,update_time,del_flag) " +
            "VALUES(#{linkStatsToday.gid}, #{linkStatsToday.fullShortUrl}, #{linkStatsToday.date},#{linkStatsToday.todayPv},#{linkStatsToday.todayUv},#{linkStatsToday.todayUip},NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE today_pv = today_pv + #{linkStatsToday.todayPv}, today_uv = today_uv + #{linkStatsToday.todayUv}, today_ip_count = today_ip_count + #{linkStatsToday.todayUip}")
    void linkStatsToday(@Param("linkStatsToday") LinkStatsTodayDO linkStatsTodayDO);


}
