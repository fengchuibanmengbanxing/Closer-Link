package com.awake.CloserLink.project.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.awake.CloserLink.project.Common.Biz.UserContext;
import com.awake.CloserLink.project.Common.Convention.Exception.ClientException;
import com.awake.CloserLink.project.Common.Convention.Exception.ServiceException;
import com.awake.CloserLink.project.Dto.Req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.awake.CloserLink.project.Dto.Req.ShortLinkGroupStatsReqDTO;
import com.awake.CloserLink.project.Dto.Req.ShortLinkStatsAccessRecordReqDTO;
import com.awake.CloserLink.project.Dto.Req.ShortLinkStatsReqDTO;
import com.awake.CloserLink.project.Dto.Resp.*;
import com.awake.CloserLink.project.Entitys.*;
import com.awake.CloserLink.project.Mapper.*;
import com.awake.CloserLink.project.Service.LinkStatsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author 清醒
 * @Date 2024/5/25 16:39
 */
@Service
public class LinkStatsServiceImpl implements LinkStatsService {
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Autowired
    private ShortLinkGotoMapper shortLinkGotoMapper;
    @Autowired
    private LinkAccessStatsMapper linkAccessStatsMapper;
    @Autowired
    private LinkLocaleStatsMapper linkLocaleStatsMapper;
    @Autowired
    private LinkBrowserStatsMapper linkBrowserStatsMapper;
    @Autowired
    private LinkOSStatsMapper linkOSStatsMapper;
    @Autowired
    private LinkAccessLogsMapper linkAccessLogsMapper;
    @Autowired
    private LinkDeviceStatsMapper linkDeviceStatsMapper;
    @Autowired
    private LinkNetworkStatsMapper linkNetworkStatsMapper;
    @Autowired
    private LinkGroupMapper linkGroupMapper;
    /**
     * 获取单个短链接监控信息
     *
     * @param shortLinkStatsReqDTO
     * @return
     */
    @Override
    public ShortLinkStatsRespDTO getOneStats(ShortLinkStatsReqDTO shortLinkStatsReqDTO) {
        /**
         * 单个短链接基础访问量
         */
        List<LinkAccessStatsDO> linkAccessStatsList = linkAccessStatsMapper.listStatsShortLink(shortLinkStatsReqDTO);
        if (CollectionUtils.isEmpty(linkAccessStatsList)) {
            return null;
        }
        LinkAccessStatsDO pvUvUidStatsByShortLink = linkAccessLogsMapper.findPvUvUidStatsByShortLink(shortLinkStatsReqDTO);
        ArrayList<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        //取出之间所有日期(年月日形式)
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(shortLinkStatsReqDTO.getStartDate()), DateUtil.parse(shortLinkStatsReqDTO.getEndDate()), DateField.DAY_OF_MONTH)
                //将日期转化为年月日
                .stream().map(DateUtil::formatDate)
                .toList();
        rangeDates.forEach(date -> linkAccessStatsList.stream().filter(item -> item.getDate().equals(date))
                .findFirst()
                .ifPresentOrElse(item -> {
                            ShortLinkStatsAccessDailyRespDTO linkAccessStatsDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(date)
                                    .uv(item.getUv())
                                    .pv(item.getPv())
                                    .uip(item.getUip())
                                    .build();
                            daily.add(linkAccessStatsDailyRespDTO);
                        }
                        , () -> {
                            ShortLinkStatsAccessDailyRespDTO linkAccessStatsDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                    .date(date)
                                    .uv(0)
                                    .pv(0)
                                    .uip(0)
                                    .build();
                            daily.add(linkAccessStatsDailyRespDTO);
                        }));

        //地区访问详情
        ArrayList<ShortLinkStatsLocaleCNRespDTO> linkLocalCNStats = new ArrayList<>();
        List<LinkLocaleStatsDO> listLinkLocaleStatsDOS = linkLocaleStatsMapper.listLocaleByShortLink(shortLinkStatsReqDTO);
        //访问总量
        int localCNSum = listLinkLocaleStatsDOS.stream().mapToInt(item -> item.getCnt()).sum();
        listLinkLocaleStatsDOS.forEach(item -> {
            double ratio = (double) item.getCnt() / localCNSum;
            //该函数将ratio乘以100.0后四舍五入为最接近的整数，然后再除以100.0，将结果转换为double类型并返回。其作用是将ratio保留两位小数。
            double ratio1 = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO shortLinkStatsLocaleCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(item.getCnt())
                    .locale(item.getProvince())
                    .ratio(ratio1)
                    .build();
            linkLocalCNStats.add(shortLinkStatsLocaleCNRespDTO);
        });

        //小时访问详情
        ArrayList<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStatsDO> listHourStatsByShortLink = linkAccessStatsMapper.listHourStatsByShortLink(shortLinkStatsReqDTO);
        for (int i = 0; i < 24; i++) {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = listHourStatsByShortLink.stream().filter(item -> item.getHour().equals(hour.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }
        //高频访问ip详情
        ArrayList<ShortLinkStatsTopIpRespDTO> topIPStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByShortLinkMap = linkAccessLogsMapper.listTopIpByShortLink(shortLinkStatsReqDTO);
        //获取访问量前五的ip及其访问量
        listTopIpByShortLinkMap.forEach(item -> {
            ShortLinkStatsTopIpRespDTO shortLinkStatsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(item.get("ip").toString())
                    .cnt(Integer.parseInt(item.get("count").toString()))
                    .build();
            topIPStats.add(shortLinkStatsTopIpRespDTO);
        });


        //一周访问详情
        ArrayList<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStatsDO> listWeekdayStatsByShortLink = linkAccessStatsMapper.listWeekdayStatsByShortLink(shortLinkStatsReqDTO);
        for (int i = 0; i < 8; i++) {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = listWeekdayStatsByShortLink.stream()
                    .filter(item -> item.getWeekday().equals(weekday.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        //浏览器访问详情
        ArrayList<ShortLinkStatsBrowserRespDTO> BrowserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByShortLinkMap = linkBrowserStatsMapper.listBrowserStatsByShortLink(shortLinkStatsReqDTO);
        int browserSum = listBrowserStatsByShortLinkMap.stream().
                mapToInt(item -> Integer.parseInt(item.get("count").toString()))
                .sum();
        listBrowserStatsByShortLinkMap.forEach(item -> {
            double ratio = (double) Integer.parseInt(item.get("count").toString()) / browserSum;
            double ratio1 = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .browser(item.get("browser").toString())
                    .cnt(Integer.parseInt(item.get("count").toString()))
                    .ratio(ratio1)
                    .build();
            BrowserStats.add(browserRespDTO);
        });
        //操作系统
        ArrayList<ShortLinkStatsOSRespDTO> OSStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByShortLinkMap = linkOSStatsMapper.listOsStatsByShortLink(shortLinkStatsReqDTO);
        int OSSum = listOsStatsByShortLinkMap.stream().mapToInt(item -> Integer.parseInt(item.get("count").toString()))
                .sum();
        listOsStatsByShortLinkMap.forEach(item -> {
            double ratio = (double) Integer.parseInt(item.get("count").toString()) / OSSum;
            double ratio1 = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOSRespDTO osRespDTO = ShortLinkStatsOSRespDTO.builder()
                    .os(item.get("os").toString())
                    .cnt(Integer.parseInt(item.get("count").toString()))
                    .ratio(ratio1)
                    .build();
            OSStats.add(osRespDTO);
        });

        //访客类型
        ArrayList<ShortLinkStatsUvRespDTO> UvTypeStats = new ArrayList<>();
        HashMap<String, Object> uvTypeCntByShortLinkMap = linkAccessLogsMapper.findUvTypeCntByShortLink(shortLinkStatsReqDTO);
        int oldUserCnt = Integer.parseInt(Optional.ofNullable(uvTypeCntByShortLinkMap).map(item -> item.get("oldUserCnt").toString())
                .map(Object::toString)
                .orElse("0")
        );
        int newUserCnt = Integer.parseInt(Optional.ofNullable(uvTypeCntByShortLinkMap).map(item -> item.get("newUserCnt").toString())
                .map(Object::toString)
                .orElse("0")
        );
        //总访问数
        int uvSum = oldUserCnt + newUserCnt;
        double oldUserRatio = (double) oldUserCnt / uvSum;
        double newUserRatio = (double) newUserCnt / uvSum;
        double oldUserRatio1 = Math.round(oldUserRatio * 100.0) / 100.0;
        double newUserRatio1 = Math.round(newUserRatio * 100.0) / 100.0;
        ShortLinkStatsUvRespDTO oldUserRespDTO = ShortLinkStatsUvRespDTO.builder()
                .uvType("oldUser")
                .cnt(oldUserCnt)
                .ratio(oldUserRatio1)
                .build();
        ShortLinkStatsUvRespDTO newUserRespDTO = ShortLinkStatsUvRespDTO.builder()
                .uvType("newUser")
                .cnt(newUserCnt)
                .ratio(newUserRatio1)
                .build();
        UvTypeStats.add(oldUserRespDTO);
        UvTypeStats.add(newUserRespDTO);

        //访问网络详情
        ArrayList<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<LinkNetworkStatsDO> listNetworkStatsByShortLink = linkNetworkStatsMapper.listNetworkStatsByShortLink(shortLinkStatsReqDTO);
        int networkSum = listNetworkStatsByShortLink.stream().mapToInt(item -> item.getCnt()).sum();
        listNetworkStatsByShortLink.forEach(item -> {
            double ratio = (double) item.getCnt() / networkSum;
            double ratio1 = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO linkStatsNetworkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .network(item.getNetwork())
                    .cnt(item.getCnt())
                    .ratio(ratio1)
                    .build();
            networkStats.add(linkStatsNetworkRespDTO);
        });
        //访问设备类型
        ArrayList<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<LinkDeviceStatsDO> listDeviceStatsByShortLink = linkDeviceStatsMapper.listDeviceStatsByShortLink(shortLinkStatsReqDTO);
        int deviceSum = listDeviceStatsByShortLink.stream().mapToInt(item -> item.getCnt()).sum();
        listDeviceStatsByShortLink.forEach(item -> {
            double ratio = (double) item.getCnt() / deviceSum;
            double ratio1 = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO linkStatsDeviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .device(item.getDevice())
                    .cnt(item.getCnt())
                    .ratio(ratio1)
                    .build();
            deviceStats.add(linkStatsDeviceRespDTO);
        });
        return ShortLinkStatsRespDTO.builder()
                .uv(pvUvUidStatsByShortLink.getUv())
                .pv(pvUvUidStatsByShortLink.getPv())
                .uip(pvUvUidStatsByShortLink.getUip())
                .daily(daily)
                .localeCnStats(linkLocalCNStats)
                .hourStats(hourStats)
                .topIpStats(topIPStats)
                .browserStats(BrowserStats)
                .weekdayStats(weekdayStats)
                .osStats(OSStats)
                .uvTypeStats(UvTypeStats)
                .networkStats(networkStats)
                .deviceStats(deviceStats)
                .build();
    }

    /**
     * 单个短链接监控分页
     * @param shortLinkStatsAccessRecordReqDTO
     * @return
     */
    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> getAccessRecordStats(ShortLinkStatsAccessRecordReqDTO shortLinkStatsAccessRecordReqDTO) {
        LambdaQueryWrapper<LinkAccessLogsDO> queryWrapper = Wrappers.lambdaQuery(LinkAccessLogsDO.class)
                .eq(LinkAccessLogsDO::getFullShortUrl, shortLinkStatsAccessRecordReqDTO.getFullShortUrl())
                .eq(LinkAccessLogsDO::getGid, shortLinkStatsAccessRecordReqDTO.getGid())
                .eq(LinkAccessLogsDO::getDelFlag, "0")
                .between(LinkAccessLogsDO::getCreateTime, shortLinkStatsAccessRecordReqDTO.getStartDate(), shortLinkStatsAccessRecordReqDTO.getEndDate())
                .orderByDesc(LinkAccessLogsDO::getCreateTime);

        IPage<LinkAccessLogsDO> page = linkAccessLogsMapper.selectPage(shortLinkStatsAccessRecordReqDTO, queryWrapper);

        IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecordPage = page.convert(item -> BeanUtil.toBean(item, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userList = shortLinkStatsAccessRecordPage.getRecords().stream().map(ShortLinkStatsAccessRecordRespDTO::getUser).toList();
        //访客类型集合
        List<Map<String, Object>> UvTypeList = linkAccessLogsMapper.selectUvTypeByUsers(shortLinkStatsAccessRecordReqDTO.getGid(),
                shortLinkStatsAccessRecordReqDTO.getFullShortUrl(),
                shortLinkStatsAccessRecordReqDTO.getEnableStatus(),
                shortLinkStatsAccessRecordReqDTO.getStartDate(),
                shortLinkStatsAccessRecordReqDTO.getEndDate(),
                userList);
        if(CollectionUtils.isEmpty(UvTypeList)){
            throw new ClientException("暂无数据");
        }
        shortLinkStatsAccessRecordPage.getRecords().stream().forEach(
                item -> {
                    String uvType = UvTypeList.stream().filter(uvType1 -> uvType1.get("user").equals(item.getUser()))
                            .findFirst().map(uvType2 -> uvType2.get("uvType").toString()).orElse("老访客");
                    item.setUvType(uvType);
                }
        );
        return shortLinkStatsAccessRecordPage;
    }





    /**
     * 短链接分组监控
     * @param requestParam
     * @return
     */
    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
//        checkGroupBelongToUser(requestParam.getGid());
        List<LinkAccessStatsDO> listStatsByGroup = linkAccessStatsMapper.listStatsByGroup(requestParam);
        if (CollUtil.isEmpty(listStatsByGroup)) {
            return null;
        }
        // 基础访问数据
        LinkAccessStatsDO pvUvUidStatsByGroup = linkAccessLogsMapper.findPvUvUidStatsByGroup(requestParam);
        // 基础访问详情
        List<ShortLinkStatsAccessDailyRespDTO> daily = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
                .map(DateUtil::formatDate)
                .toList();
        rangeDates.forEach(each -> listStatsByGroup.stream()
                .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate())))
                .findFirst()
                .ifPresentOrElse(item -> {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                            .date(each)
                            .pv(item.getPv())
                            .uv(item.getUv())
                            .uip(item.getUip())
                            .build();
                    daily.add(accessDailyRespDTO);
                }, () -> {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                            .date(each)
                            .pv(0)
                            .uv(0)
                            .uip(0)
                            .build();
                    daily.add(accessDailyRespDTO);
                }));
        // 地区访问详情（仅国内）
        List<ShortLinkStatsLocaleCNRespDTO> localeCnStats = new ArrayList<>();
        List<LinkLocaleStatsDO> listedLocaleByGroup = linkLocaleStatsMapper.listLocaleByGroup(requestParam);
        int localeCnSum = listedLocaleByGroup.stream()
                .mapToInt(LinkLocaleStatsDO::getCnt)
                .sum();
        listedLocaleByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / localeCnSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(each.getCnt())
                    .locale(each.getProvince())
                    .ratio(actualRatio)
                    .build();
            localeCnStats.add(localeCNRespDTO);
        });
        // 小时访问详情
        List<Integer> hourStats = new ArrayList<>();
        List<LinkAccessStatsDO> listHourStatsByGroup = linkAccessStatsMapper.listHourStatsByGroup(requestParam);
        for (int i = 0; i < 24; i++) {
            AtomicInteger hour = new AtomicInteger(i);
            int hourCnt = listHourStatsByGroup.stream()
                    .filter(each -> Objects.equals(each.getHour(), hour.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }
        // 高频访问IP详情
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String, Object>> listTopIpByGroup = linkAccessLogsMapper.listTopIpByGroup(requestParam);
        listTopIpByGroup.forEach(each -> {
            ShortLinkStatsTopIpRespDTO statsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            topIpStats.add(statsTopIpRespDTO);
        });
        // 一周访问详情
        List<Integer> weekdayStats = new ArrayList<>();
        List<LinkAccessStatsDO> listWeekdayStatsByGroup = linkAccessStatsMapper.listWeekdayStatsByGroup(requestParam);
        for (int i = 1; i < 8; i++) {
            AtomicInteger weekday = new AtomicInteger(i);
            int weekdayCnt = listWeekdayStatsByGroup.stream()
                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                    .findFirst()
                    .map(LinkAccessStatsDO::getPv)
                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        // 浏览器访问详情
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listBrowserStatsByGroup = linkBrowserStatsMapper.listBrowserStatsByGroup(requestParam);
        int browserSum = listBrowserStatsByGroup.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listBrowserStatsByGroup.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .browser(each.get("browser").toString())
                    .ratio(actualRatio)
                    .build();
            browserStats.add(browserRespDTO);
        });
        // 操作系统访问详情
        List<ShortLinkStatsOSRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listOsStatsByGroup = linkOSStatsMapper.listOsStatsByGroup(requestParam);
        int osSum = listOsStatsByGroup.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listOsStatsByGroup.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOSRespDTO osRespDTO = ShortLinkStatsOSRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .os(each.get("os").toString())
                    .ratio(actualRatio)
                    .build();
            osStats.add(osRespDTO);
        });
        // 访问设备类型详情
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<LinkDeviceStatsDO> listDeviceStatsByGroup = linkDeviceStatsMapper.listDeviceStatsByGroup(requestParam);
        int deviceSum = listDeviceStatsByGroup.stream()
                .mapToInt(LinkDeviceStatsDO::getCnt)
                .sum();
        listDeviceStatsByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / deviceSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .cnt(each.getCnt())
                    .device(each.getDevice())
                    .ratio(actualRatio)
                    .build();
            deviceStats.add(deviceRespDTO);
        });
        // 访问网络类型详情
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<LinkNetworkStatsDO> listNetworkStatsByGroup = linkNetworkStatsMapper.listNetworkStatsByGroup(requestParam);
        int networkSum = listNetworkStatsByGroup.stream()
                .mapToInt(LinkNetworkStatsDO::getCnt)
                .sum();
        listNetworkStatsByGroup.forEach(each -> {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .cnt(each.getCnt())
                    .network(each.getNetwork())
                    .ratio(actualRatio)
                    .build();
            networkStats.add(networkRespDTO);
        });
        return ShortLinkStatsRespDTO.builder()
                .pv(pvUvUidStatsByGroup.getPv())
                .uv(pvUvUidStatsByGroup.getUv())
                .uip(pvUvUidStatsByGroup.getUip())
                .daily(daily)
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .build();
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
//        checkGroupBelongToUser(requestParam.getGid());
        IPage<LinkAccessLogsDO> linkAccessLogsDOIPage = linkAccessLogsMapper.selectGroupPage(requestParam);
        if (CollUtil.isEmpty(linkAccessLogsDOIPage.getRecords())) {
            return new Page<>();
        }
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = linkAccessLogsDOIPage
                .convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .toList();
        List<Map<String, Object>> uvTypeList = linkAccessLogsMapper.selectGroupUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        actualResult.getRecords().forEach(each -> {
            String uvType = uvTypeList.stream()
                    .filter(item -> Objects.equals(each.getUser(), item.get("user")))
                    .findFirst()
                    .map(item -> item.get("uvType"))
                    .map(Object::toString)
                    .orElse("旧访客");
            each.setUvType(uvType);
        });
        return actualResult;
    }


    /**
     * 校验用户登录状态与分组信息是否存在
     * @param gid
     * @throws ServiceException
     */
    public void checkGroupBelongToUser(String gid) throws ServiceException {
        String username = UserContext.getUsername();
        username = Optional.ofNullable(username)
                .orElseThrow(() -> new ServiceException("用户未登录"));
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, username);
        List<GroupDO> groupDOList = linkGroupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户信息与分组标识不匹配");
        }
    }




}
