package com_awake_CloserLink.Mapper;

import com_awake_CloserLink.Entitys.LinkDeviceStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 清醒
 * @Date 2024/5/24 20:46
 */
@Mapper
public interface LinkDeviceStatsMapper {

    /**
     * 记录设备访问记录
     */
    @Insert("INSERT INTO " +
            "t_link_device_stats (gid,full_short_url, date, cnt,device, create_time, update_time, del_flag) " +
            "VALUES(#{linkDeviceStats.gid}, #{linkDeviceStats.fullShortUrl}, #{linkDeviceStats.date}, #{linkDeviceStats.cnt},#{linkDeviceStats.device}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkDeviceStats.cnt};")
    void shortLinkStatsDevice(@Param("linkDeviceStats") LinkDeviceStatsDO linkDeviceStatsDO);

}
