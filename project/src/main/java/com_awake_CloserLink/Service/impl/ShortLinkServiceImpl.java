package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Convention.Exception.ClientException;
import com_awake_CloserLink.Common.Convention.Exception.ServiceException;
import com_awake_CloserLink.Common.Enums.VailDateTypeEnum;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkUpdateReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.*;
import com_awake_CloserLink.Mapper.*;
import com_awake_CloserLink.Service.ShortLinkService;
import com_awake_CloserLink.Utils.HashUtil;
import com_awake_CloserLink.Utils.LinkUtil;
import com_awake_CloserLink.Utils.SslUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com_awake_CloserLink.Common.Constant.RedisKeyConstant.*;


/**
 * @Author 清醒
 * @Date 2024/5/18 9:53
 */
@Service
@Slf4j
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, LinkDO> implements ShortLinkService {
    //创建短链接布隆过滤器
    @Autowired
    private RBloomFilter<String> shortLinkCreatCachePenetrationBloomFilter;
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    @Autowired
    private ShortLinkGotoMapper shortLinkGotoMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
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
    private LinkStatsTodayMapper linkStatsTodayMapper;
    @Value("${shortLink.amap.key}")
    private String amapKey;
    @Value("${shortLink.domain.default}")
    private String createDomainDefault;


    //创建短链接
    @Override
    public ShortLinkCreatRespDTO creatShortLink(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        //短链接后缀
        String generateSuffix = generateSuffix(shortLinkCreatReqDTO);
        LinkDO shortLinkDO = BeanUtil.toBean(shortLinkCreatReqDTO, LinkDO.class);
        String fullShortUrl ;
        StringBuilder stringBuilder = new StringBuilder(createDomainDefault);
        fullShortUrl=stringBuilder.append("/").append(generateSuffix).toString();
        if (shortLinkCreatCachePenetrationBloomFilter.contains(fullShortUrl)) {
            throw new ClientException("短链接已经存在！");
        }
        //设置完整短链接
        shortLinkDO.setDomain(createDomainDefault);
        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setEnableStatus(1);
        shortLinkDO.setShortUri(generateSuffix);
        shortLinkDO.setTotalPv(0);
        shortLinkDO.setTotalUv(0);
        shortLinkDO.setTotalUip(0);
        shortLinkDO.setDelFlag(0);
        //获取网站图标
        shortLinkDO.setFavicon(SslUtils.getFaviconUrl(shortLinkCreatReqDTO.getOriginUrl()));

        LinkGotoDO linkGotoDO = LinkGotoDO.builder()
//                .id(shortLinkDO.getId())
                .gid(shortLinkDO.getGid())
                .fullShortUrl(fullShortUrl)
                .build();

        //防止数据库写入后但是布隆过滤器未添加短链接导致下次请求短链接缓存穿透
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        } catch (Exception e) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
            if (linkDO != null) {
                log.error("数据库写入失败，请检查！");
                throw new ServiceException("短链接生成重复！");
            }
        }
        //缓存预热  创建短链接后就将其放入redis中
        stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), shortLinkDO.getOriginUrl(), LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS);
        shortLinkCreatCachePenetrationBloomFilter.add(fullShortUrl);

        //返回响应类RespDTO
        //流式创建对象
        return ShortLinkCreatRespDTO.builder()
                .gid(shortLinkCreatReqDTO.getGid())
                .originUrl(shortLinkCreatReqDTO.getOriginUrl())
                .fullShortUrl(shortLinkCreatReqDTO.getDomainProtocol() + shortLinkDO.getFullShortUrl())
                .build();

    }


    //获取短链接分页

    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        String gid = shortLinkPageReqDTO.getGid();
        if (gid != null) {
            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                    .eq(LinkDO::getGid, gid)
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 1);
            IPage<LinkDO> linkDOIPage = baseMapper.selectPage(shortLinkPageReqDTO, queryWrapper);
            return linkDOIPage.convert(linkDO -> {
                ShortLinkPageRespDTO shortLinkPageRespDTO = new ShortLinkPageRespDTO();
                BeanUtil.copyProperties(linkDO, shortLinkPageRespDTO);
                return shortLinkPageRespDTO;
            });
        }
        return null;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> countShortLink(List<String> requestParam) {
        QueryWrapper<LinkDO> queryWrapper = Wrappers.query(new LinkDO())
                .select("gid,count(*) as shortLinkCount")//不取别名属性无法对应
                .in("gid", requestParam)
                .eq("enable_status", 1)
                .groupBy("gid");
        List<Map<String, Object>> list = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(list, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO shortLinkUpdateReqDTO) {
        LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                .eq(LinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                .eq(LinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                .eq(LinkDO::getDelFlag, 0)
                .eq(LinkDO::getEnableStatus, 1);
        LinkDO hasLinkDO = baseMapper.selectOne(queryWrapper);
        //当更改分组id时数据库将无法查询出数据
        if (hasLinkDO == null) {
            throw new ClientException("短链接记录不存在！");
        }
        LinkDO linkdo = LinkDO.builder()
                .gid(shortLinkUpdateReqDTO.getGid())
                .fullShortUrl(shortLinkUpdateReqDTO.getFullShortUrl())
                .originUrl(shortLinkUpdateReqDTO.getOriginUrl())
                .describe(shortLinkUpdateReqDTO.getDescribe())
                .domain(shortLinkUpdateReqDTO.getDomain())
                .favicon(shortLinkUpdateReqDTO.getFavicon())
                .validDate(shortLinkUpdateReqDTO.getValidDate())
                .validDateType(shortLinkUpdateReqDTO.getValidDateType())
                .build();
        //判断数据库中与新数据gid是否一致
        if (Objects.equals(hasLinkDO.getGid(), shortLinkUpdateReqDTO.getGid())) {
            LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, hasLinkDO.getGid())
                    .eq(LinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 1)
                    .set(Objects.equals(shortLinkUpdateReqDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), LinkDO::getValidDate, null);
            baseMapper.update(linkdo, updateWrapper);
        } else {
            LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                    .eq(LinkDO::getOriginUrl, shortLinkUpdateReqDTO.getOriginUrl())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 1)
                    .set(Objects.equals(shortLinkUpdateReqDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), LinkDO::getValidDate, null);
            //涉及两个操作要加声明式事务
            baseMapper.delete(updateWrapper);
            linkdo.setGid(shortLinkUpdateReqDTO.getGid());
            baseMapper.insert(linkdo);
        }
    }

    /**
     * 短链接跳转真实网址
     *
     * @param shortLink 短网址
     * @param request   http请求
     * @param response  http响应
     */
    @Override
    public void restoreUrl(String shortLink, ServletRequest request, ServletResponse response) throws IOException {
        //获取主机名
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        //完整短链接
        String fullShortLink = serverName +":"+serverPort+ "/" + shortLink;
        //创建短链接时就已经加入布隆过滤器了
        boolean contains = shortLinkCreatCachePenetrationBloomFilter.contains(fullShortLink);
        //不存在直接返回
        if (!contains) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }

        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            shortLinkStats(fullShortLink, null, request, response);
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }

        //存在则查询redis缓存判断是否为空对象
        String isNotnull = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NOTNULL_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(isNotnull)) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }

        if (StrUtil.isBlank(originalLink)) {
            RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortLink));
            lock.lock();
            //缓存击穿加锁
            try {
                //双重判定锁
                originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
                if (StrUtil.isNotBlank(originalLink)) {
                    shortLinkStats(fullShortLink, null, request, response);
                    ((HttpServletResponse) response).sendRedirect(originalLink);
                    return;
                }
                //优先查询goto表
                LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(Wrappers.lambdaQuery(LinkGotoDO.class)
                        .eq(LinkGotoDO::getFullShortUrl, fullShortLink));
                if (linkGotoDO == null) {
                    //短链接不存在设置空对象
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NOTNULL_SHORT_LINK_KEY, fullShortLink), "-");
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    //封控
                }
                LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
                        .eq(LinkDO::getFullShortUrl, linkGotoDO.getFullShortUrl())
                        .eq(LinkDO::getDelFlag, 0)
                        .eq(LinkDO::getEnableStatus, 1)
                        .eq(LinkDO::getGid, linkGotoDO.getGid());
                LinkDO linkDO = baseMapper.selectOne(queryWrapper);
                //网址跳转

                //短链接过期 创建短链接空对象   防止永久短链接有效期设置为空导致错误
                if (linkDO == null || (linkDO.getValidDate() != null && linkDO.getValidDate().getTime() < System.currentTimeMillis())) {
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NOTNULL_SHORT_LINK_KEY, fullShortLink), "-");
                    ((HttpServletResponse) response).sendRedirect("/page/notfound");
                    return;
                }
                //过期后再设置默认时间()
                stringRedisTemplate.opsForValue()
                        .set(String.format(GOTO_SHORT_LINK_KEY, fullShortLink), linkDO.getOriginUrl(), LinkUtil.getLinkCacheValidTime(linkDO.getValidDate()), TimeUnit.MILLISECONDS);
                shortLinkStats(fullShortLink, linkDO.getGid(), request, response);
                ((HttpServletResponse) response).sendRedirect(linkDO.getOriginUrl());

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageLink(ShortLinkPageReqDTO shortLinkPageReqDTO) {
        IPage<LinkDO> result = baseMapper.pageLink(shortLinkPageReqDTO);
        return result.convert(item -> {
            ShortLinkPageRespDTO shortLinkPageRespDTO = BeanUtil.toBean(item, ShortLinkPageRespDTO.class);
            return shortLinkPageRespDTO;
        });
    }

    //统计
    public void shortLinkStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response) {
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicBoolean uvIsFlag = new AtomicBoolean();
        AtomicReference<String> uv = new AtomicReference<>();
        try {
            Runnable runnable = () -> {
                //快速生成uuid
                uv.set(UUID.fastUUID().toString());
                Cookie uvCookie = new Cookie("uv", uv.get());
                //设置cookie有效期30天
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
                ((HttpServletResponse) response).addCookie(uvCookie);
                stringRedisTemplate.opsForSet().add("short-link:stats:uv" + fullShortUrl, uv.get());
                //是否为新用户
                uvIsFlag.set(true);
            };

            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("uv"))
                        .findFirst()
                        .map(cookie -> cookie.getValue())
                        .ifPresentOrElse(each -> {
                            Long add = stringRedisTemplate.opsForSet().add("short-link:stats:uv" + fullShortUrl, each);
                            uv.set(each);
                            uvIsFlag.set((add != null && add > 0));
                        }, runnable);
            } else {
                runnable.run();
            }
            //统计ip地址
            AtomicBoolean ipIsFlag = new AtomicBoolean();
            String ipaddr = LinkUtil.getIp((HttpServletRequest) request);
            Long addIp = stringRedisTemplate.opsForSet().add("short-link:stats:ip" + fullShortUrl, ipaddr);
            ipIsFlag.set((addIp != null && addIp > 0));

            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<LinkGotoDO> queryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                        .eq(LinkGotoDO::getFullShortUrl, fullShortUrl);
                LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = linkGotoDO.getGid();
            }
            Date date = new Date();
            int hour = DateUtil.hour(date, true);
            int week = DateUtil.dayOfWeek(date);
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .hour(hour)
                    .fullShortUrl(fullShortUrl)
                    .uv(uvIsFlag.get() ? 1 : 0)
                    .pv(1)
                    .uip(ipIsFlag.get() ? 1 : 0)
                    .gid(gid)
                    .weekday(week)
                    .date(date)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            //统计中国地图地址
            HashMap<String, Object> localeStatsMap = new HashMap<>();
            localeStatsMap.put("ip", ipaddr);
            localeStatsMap.put("key", amapKey);
            String localeStatsAmap = HttpUtil.get(AMAP_REMOTE_KEY, localeStatsMap);
            //ip地址获取
            JSONObject localeStatsJsonObject = JSON.parseObject(localeStatsAmap);
            String infoCode = localeStatsJsonObject.getString("infocode");
            String city = localeStatsJsonObject.getString("city");
            String adCode = localeStatsJsonObject.getString("adcode");
            String province = localeStatsJsonObject.getString("province");
            //是否有省份信息
            boolean unKnownFlag = StrUtil.isBlank(province);
            if (StrUtil.isNotBlank(infoCode) && infoCode.equals("10000")) {
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .fullShortUrl(fullShortUrl)
                        .gid(gid)
                        .date(date)
                        .adcode(unKnownFlag ? adCode : "未知")
                        .city(unKnownFlag ? city : "未知")
                        .province(unKnownFlag ? province : "未知")
                        .country("中国")
                        .cnt(1)
                        .build();
                linkLocaleStatsMapper.shortLinkStatsIp(linkLocaleStatsDO);
            }
            //统计操作系统
            String os = LinkUtil.getOS((HttpServletRequest) request);
            LinkOSStatsDO linkOSStatsDO = LinkOSStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(date)
                    .os(os)
                    .build();
            linkOSStatsMapper.shortLinkStatsOS(linkOSStatsDO);

            //统计浏览器来源
            String browser = LinkUtil.getBrowser((HttpServletRequest) request);
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .cnt(1)
                    .date(date)
                    .browser(browser)
                    .build();
            linkBrowserStatsMapper.shortLinkStatsBrowser(linkBrowserStatsDO);


            //统计访问设备
            String device = LinkUtil.getDeviceType((HttpServletRequest) request);
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .cnt(1)
                    .device(device)
                    .build();
            linkDeviceStatsMapper.shortLinkStatsDevice(linkDeviceStatsDO);


            //统计访问网络
            String network = LinkUtil.getNetwork((HttpServletRequest) request);
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .cnt(1)
                    .network(network)
                    .build();
            linkNetworkStatsMapper.shortLinkStatsIp(linkNetworkStatsDO);

            //统计高频ip
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .ip(ipaddr)
                    .gid(gid)
                    .user(uv.get())
                    .browser(browser)
                    .os(os)
                    .fullShortUrl(fullShortUrl)
                    .device(device)
                    .network(network)
                    .locale(StrUtil.join("-", "中国", unKnownFlag ? province : "未知", unKnownFlag ? city : "未知", unKnownFlag ? adCode : "未知"))
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);
            baseMapper.incrementStats(gid, fullShortUrl, uvIsFlag.get() ? 1 : 0, 1, ipIsFlag.get() ? 1 : 0);

            //监控当日信息
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .gid(gid)
                    .date(date)
                    .fullShortUrl(fullShortUrl)
                    .todayPv(1)
                    .todayUv(uvIsFlag.get() ? 1 : 0)
                    .todayUip(ipIsFlag.get() ? 1 : 0)
                    .build();
            linkStatsTodayMapper.linkStatsToday(linkStatsTodayDO);

        } catch (Exception e) {
            throw new ClientException("统计异常！");
        }
    }


    //工具类实现短链接
    private String generateSuffix(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        int creatgenerateSuffix = 0;
        String originUrl = shortLinkCreatReqDTO.getOriginUrl();
        String shortUri;
        while (true) {
            if (creatgenerateSuffix > 10) {
                throw new ServiceException("多次创建短链接，请稍后重试！");
            }
            //相同网址可能由于毫秒数不同可以创建多个链接
            originUrl += System.currentTimeMillis();
            //该工具类只要内容不更改生成的数据仍是一样
            shortUri = HashUtil.hashToBase62(originUrl);
            //布隆过滤器不包含一定就不包含
            if (!shortLinkCreatCachePenetrationBloomFilter.contains(shortLinkCreatReqDTO.getDomain() + "/" + shortUri)) {
                break;
            }
            creatgenerateSuffix++;
        }
        return shortUri;
    }
}
