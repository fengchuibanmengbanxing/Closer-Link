package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Entitys.LinkBrowserStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

}
