package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Dto.Req.ShortLinkGroupStatsReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkStatsReqDTO;
import com_awake_CloserLink.Entitys.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * @Author 清醒
 * @Date 2024/5/24 16:19
 */
@Mapper
public interface LinkBrowserStatsMapper {

    /**
     * 记录浏览器访问记录
     */
    @Insert("INSERT INTO " +
            "t_link_browser_stats (gid,full_short_url, date, cnt,browser, create_time, update_time, del_flag) " +
            "VALUES(#{linkBrowserStats.gid}, #{linkBrowserStats.fullShortUrl}, #{linkBrowserStats.date}, #{linkBrowserStats.cnt},#{linkBrowserStats.browser}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkBrowserStats.cnt};")
    void shortLinkStatsBrowser(@Param("linkBrowserStats") LinkBrowserStatsDO linkBrowserStatsDO);


    /**
     * 根据短链接获取指定日期内浏览器监控数据
     */
    @Select("SELECT " +
            "    tlbs.browser, " +
            "    SUM(tlbs.cnt) AS count " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_browser_stats tlbs ON tl.full_short_url = tlbs.full_short_url " +
            "WHERE " +
            "    tlbs.full_short_url = #{param.fullShortUrl} " +
            "    AND tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = #{param.enableStatus} " +
            "    AND tlbs.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tlbs.full_short_url, tl.gid, tlbs.browser;")
    List<HashMap<String, Object>> listBrowserStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内浏览器监控数据
     */
    @Select("SELECT " +
            "    tlbs.browser, " +
            "    SUM(tlbs.cnt) AS count " +
            "FROM " +
            "    t_link tl INNER JOIN " +
            "    t_link_browser_stats tlbs ON tl.full_short_url = tlbs.full_short_url " +
            "WHERE " +
            "    tl.gid = #{param.gid} " +
            "    AND tl.del_flag = '0' " +
            "    AND tl.enable_status = '0' " +
            "    AND tlbs.date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    tl.gid, tlbs.browser;")
    List<HashMap<String, Object>> listBrowserStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);


}
