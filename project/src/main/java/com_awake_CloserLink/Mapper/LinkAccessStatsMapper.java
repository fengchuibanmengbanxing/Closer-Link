package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Dto.Req.ShortLinkStatsReqDTO;
import com_awake_CloserLink.Entitys.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/22 21:35
 */
@Mapper
public interface LinkAccessStatsMapper {
    /**
     * 记录基础访问监控数据
     */
    @Insert("INSERT INTO " +
            "t_link_access_stats (gid,full_short_url, date, pv, uv, uip, hour, weekday, create_time, update_time, del_flag) " +
            "VALUES(#{linkAccessStats.gid}, #{linkAccessStats.fullShortUrl}, #{linkAccessStats.date}, #{linkAccessStats.pv}, #{linkAccessStats.uv}, #{linkAccessStats.uip}, #{linkAccessStats.hour}, #{linkAccessStats.weekday}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE pv = pv +  #{linkAccessStats.pv}, uv = uv + #{linkAccessStats.uv}, uip = uip + #{linkAccessStats.uip};")
    void shortLinkStats(@Param("linkAccessStats") LinkAccessStatsDO linkAccessStatsDO);


    @Select("SELECT * "+
            "FROM "+
            "t_link_access_stats " +
            "WHERE "+
            "gid=#{shortLinkStats.gid} " +
            "AND full_short_url=#{shortLinkStats.fullShortUrl} " +
            "AND update_time BETWEEN #{shortLinkStats.startDate} and  #{shortLinkStats.endDate}")
    List<LinkAccessStatsDO> listStatsShortLink(@Param("shortLinkStats") ShortLinkStatsReqDTO shortLinkStatsReqDTO);


    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    tlas.date, " +
            "    SUM(tlas.pv) AS pv, " +
            "    SUM(tlas.uv) AS uv, " +
            "    SUM(tlas.uip) AS uip " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tl.gid, tlas.date;")
    List<LinkAccessStatsDO> listStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);
//
//    /**
//     * 根据分组获取指定日期内基础监控数据
//     */
//    @Select("SELECT " +
//            "    tlas.date, " +
//            "    SUM(tlas.pv) AS pv, " +
//            "    SUM(tlas.uv) AS uv, " +
//            "    SUM(tlas.uip) AS uip " +
//            "FROM " +
//            "    t_link tl INNER JOIN " +
//            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
//            "WHERE " +
//            "    tl.gid = #{param.gid} " +
//            "    AND tl.del_flag = '0' " +
//            "    AND tl.enable_status = '0' " +
//            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    tl.gid, tlas.date;")
//    List<LinkAccessStatsDO> listStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.hour, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tl.gid, tlas.hour;")
    List<LinkAccessStatsDO> listHourStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

//    /**
//     * 根据分组获取指定日期内小时基础监控数据
//     */
//    @Select("SELECT " +
//            "    tlas.hour, " +
//            "    SUM(tlas.pv) AS pv " +
//            "FROM " +
//            "    t_link tl INNER JOIN " +
//            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
//            "WHERE " +
//            "    tl.gid = #{param.gid} " +
//            "    AND tl.del_flag = '0' " +
//            "    AND tl.enable_status = '0' " +
//            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    tl.gid, tlas.hour;")
//    List<LinkAccessStatsDO> listHourStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);

    /**
     * 根据短链接获取指定日期内小时基础监控数据
     */
    @Select("SELECT " +
            "    tlas.weekday, " +
            "    SUM(tlas.pv) AS pv " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
            "WHERE " +
            "    tlas.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlas.full_short_url, tl.gid, tlas.weekday;")
    List<LinkAccessStatsDO> listWeekdayStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

//    /**
//     * 根据分组获取指定日期内小时基础监控数据
//     */
//    @Select("SELECT " +
//            "    tlas.weekday, " +
//            "    SUM(tlas.pv) AS pv " +
//            "FROM " +
//            "    t_link tl INNER JOIN " +
//            "    t_link_access_stats tlas ON tl.full_short_url = tlas.full_short_url " +
//            "WHERE " +
//            "    tl.gid = #{param.gid} " +
//            "    AND tl.del_flag = '0' " +
//            "    AND tl.enable_status = '0' " +
//            "    AND tlas.date BETWEEN #{param.startDate} and #{param.endDate} " +
//            "GROUP BY " +
//            "    tl.gid, tlas.weekday;")
//    List<LinkAccessStatsDO> listWeekdayStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
//

}



