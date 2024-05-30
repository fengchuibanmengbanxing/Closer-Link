package com_awake_CloserLink.Service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com_awake_CloserLink.Common.Config.GotoDomainWhiteListConfiguration;
import com_awake_CloserLink.Common.Convention.Exception.ClientException;
import com_awake_CloserLink.Common.Convention.Exception.ServiceException;
import com_awake_CloserLink.Common.Enums.VailDateTypeEnum;
import com_awake_CloserLink.Dto.Biz.ShortLinkStatsRecordDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkCreatReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkPageReqDTO;
import com_awake_CloserLink.Dto.Req.ShortLinkUpdateReqDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkCreatRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkGroupCountQueryRespDTO;
import com_awake_CloserLink.Dto.Resp.ShortLinkPageRespDTO;
import com_awake_CloserLink.Entitys.LinkDO;
import com_awake_CloserLink.Entitys.LinkGotoDO;
import com_awake_CloserLink.Mapper.*;
import com_awake_CloserLink.Mq.producer.ShortLinkStatsSaveProducer;
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
import org.redisson.api.RReadWriteLock;
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
    @Autowired
    private ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    @Autowired
    private GotoDomainWhiteListConfiguration gotoDomainWhiteListConfiguration;
    @Value("${shortLink.amap.key}")
    private String amapKey;
    @Value("${shortLink.domain.default}")
    private String createDomainDefault;


    //创建短链接
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreatRespDTO creatShortLink(ShortLinkCreatReqDTO shortLinkCreatReqDTO) {
        verificationWhitelist(shortLinkCreatReqDTO.getOriginUrl());
        //短链接后缀
        String generateSuffix = generateSuffix(shortLinkCreatReqDTO);
        LinkDO shortLinkDO = BeanUtil.toBean(shortLinkCreatReqDTO, LinkDO.class);
        String fullShortUrl;
        StringBuilder stringBuilder = new StringBuilder(createDomainDefault);
        fullShortUrl = stringBuilder.append("/").append(generateSuffix).toString();
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
//
//            LambdaQueryWrapper<LinkDO> queryWrapper = Wrappers.lambdaQuery(LinkDO.class)
//                    .eq(LinkDO::getFullShortUrl, fullShortUrl);
//            LinkDO linkDO = baseMapper.selectOne(queryWrapper);
//            if (linkDO != null) {
            log.error("数据库写入失败，请检查！");
            throw new ServiceException("短链接生成重复！");
//            }
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
        verificationWhitelist(shortLinkUpdateReqDTO.getOriginUrl());
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

        //判断数据库中与新数据gid是否一致
        if (Objects.equals(hasLinkDO.getGid(), shortLinkUpdateReqDTO.getGid())) {
            LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                    .eq(LinkDO::getGid, hasLinkDO.getGid())
                    .eq(LinkDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                    .eq(LinkDO::getDelFlag, 0)
                    .eq(LinkDO::getEnableStatus, 1)
                    .eq(LinkDO::getDelTime, 0L)
                    .set(Objects.equals(shortLinkUpdateReqDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), LinkDO::getValidDate, null);
            LinkDO linkdo = LinkDO.builder()
                    .gid(shortLinkUpdateReqDTO.getGid())
                    .fullShortUrl(shortLinkUpdateReqDTO.getFullShortUrl())
                    .originUrl(shortLinkUpdateReqDTO.getOriginUrl())
                    .describe(shortLinkUpdateReqDTO.getDescribe())
                    .domain(shortLinkUpdateReqDTO.getDomain())
                    .favicon(shortLinkUpdateReqDTO.getFavicon())
                    .createdType(hasLinkDO.getCreatedType())
                    .validDate(shortLinkUpdateReqDTO.getValidDate())
                    .validDateType(shortLinkUpdateReqDTO.getValidDateType())
                    .build();
            baseMapper.update(linkdo, updateWrapper);
        } else {
            // 为什么监控表要加上Gid？不加的话是否就不存在读写锁？详情查看：https://nageoffer.com/shortlink/question
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, shortLinkUpdateReqDTO.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();

            try {
                //为旧数据添加过期设置时间
                LambdaUpdateWrapper<LinkDO> updateWrapper = Wrappers.lambdaUpdate(LinkDO.class)
                        .eq(LinkDO::getGid, shortLinkUpdateReqDTO.getGid())
                        .eq(LinkDO::getOriginUrl, shortLinkUpdateReqDTO.getOriginUrl())
                        .eq(LinkDO::getDelFlag, 0)
                        .eq(LinkDO::getEnableStatus, 1);
                LinkDO delShortLinkDO = LinkDO.builder()
                        .delTime(System.currentTimeMillis())
                        .build();
                delShortLinkDO.setDelFlag(1);
                baseMapper.update(delShortLinkDO, updateWrapper);

                LinkDO linkDO = LinkDO.builder()
                        .domain(createDomainDefault)
                        .originUrl(shortLinkUpdateReqDTO.getOriginUrl())
                        .gid(shortLinkUpdateReqDTO.getGid())
                        .createdType(hasLinkDO.getCreatedType())
                        .validDateType(shortLinkUpdateReqDTO.getValidDateType())
                        .validDate(shortLinkUpdateReqDTO.getValidDate())
                        .describe(shortLinkUpdateReqDTO.getDescribe())
                        .shortUri(hasLinkDO.getShortUri())
                        .enableStatus(hasLinkDO.getEnableStatus())
                        .totalPv(hasLinkDO.getTotalPv())
                        .totalUv(hasLinkDO.getTotalUv())
                        .totalUip(hasLinkDO.getTotalUip())
                        .fullShortUrl(hasLinkDO.getFullShortUrl())
                        .favicon(SslUtils.getFaviconUrl(shortLinkUpdateReqDTO.getOriginUrl()))
                        .delTime(0L)
                        .build();
                baseMapper.insert(linkDO);
                //由于gid发生改变因此需要更给goto表的关系链接
                LambdaQueryWrapper<LinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(LinkGotoDO.class)
                        .eq(LinkGotoDO::getFullShortUrl, shortLinkUpdateReqDTO.getFullShortUrl())
                        .eq(LinkGotoDO::getGid, hasLinkDO.getGid());
                //查询更改前数据
                LinkGotoDO linkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
                shortLinkGotoMapper.delete(linkGotoQueryWrapper);
                //设置新gid
                linkGotoDO.setGid(shortLinkUpdateReqDTO.getGid());
                shortLinkGotoMapper.insert(linkGotoDO);

            } finally {
                //释放读写锁
                rLock.unlock();
            }
        }
        //新数据与旧数据有效类型，过期时间，原始网址是否相等
        if (!Objects.equals(hasLinkDO.getValidDateType(), shortLinkUpdateReqDTO.getValidDateType())
                || !Objects.equals(hasLinkDO.getValidDate(), shortLinkUpdateReqDTO.getValidDate())
                || !Objects.equals(hasLinkDO.getOriginUrl(), shortLinkUpdateReqDTO.getOriginUrl())) {
            //不相等删除关联表数据
            stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, shortLinkUpdateReqDTO.getFullShortUrl()));
            Date currentDate = new Date();
            //若设置的过期时间在当前时间之后则删除redis空对象
            if (hasLinkDO.getValidDate() != null && hasLinkDO.getValidDate().before(currentDate)) {
                if (Objects.equals(shortLinkUpdateReqDTO.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()) || shortLinkUpdateReqDTO.getValidDate().after(currentDate)) {
                    stringRedisTemplate.delete(String.format(GOTO_IS_NOTNULL_SHORT_LINK_KEY, shortLinkUpdateReqDTO.getFullShortUrl()));
                }
            }
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
        String fullShortLink = serverName + ":" + serverPort + "/" + shortLink;
        //创建短链接时就已经加入布隆过滤器了
        boolean contains = shortLinkCreatCachePenetrationBloomFilter.contains(fullShortLink);
        //不存在直接返回
        if (!contains) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }
        ShortLinkStatsRecordDTO statsRecord = buildLinkStatsRecordAndSetUser(fullShortLink, request, response);

        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortLink));
        if (StrUtil.isNotBlank(originalLink)) {
            shortLinkStats(fullShortLink, null, statsRecord);
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
                    shortLinkStats(fullShortLink, null, statsRecord);
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
                shortLinkStats(fullShortLink, linkDO.getGid(), statsRecord);
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
    public void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO statsRecord) {
        Map<String, String> producerMap = new HashMap<>();
        producerMap.put("fullShortUrl", fullShortUrl);
        producerMap.put("gid", gid);
        producerMap.put("statsRecord", JSON.toJSONString(statsRecord));
        shortLinkStatsSaveProducer.send(producerMap);
    }


    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request, ServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        AtomicReference<String> uv = new AtomicReference<>();
        Runnable addResponseCookieTask = () -> {
            uv.set(UUID.randomUUID().toString());
            Cookie uvCookie = new Cookie("uv", uv.get());
            uvCookie.setMaxAge(60 * 60 * 24 * 30);
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
            ((HttpServletResponse) response).addCookie(uvCookie);
            uvFirstFlag.set(Boolean.TRUE);
            stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv.get());
        };
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresentOrElse(each -> {
                        uv.set(each);
                        Long uvAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, each);
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addResponseCookieTask);
        } else {
            addResponseCookieTask.run();
        }
        String remoteAddr = LinkUtil.getIp(((HttpServletRequest) request));
        String os = LinkUtil.getOS(((HttpServletRequest) request));
        String browser = LinkUtil.getBrowser(((HttpServletRequest) request));
        String device = LinkUtil.getDeviceType(((HttpServletRequest) request));
        String network = LinkUtil.getNetwork(((HttpServletRequest) request));
        Long uipAdded = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, remoteAddr);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(uv.get())
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .build();
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
//            originUrl += System.currentTimeMillis(); 使用毫秒数仍有可能再同一时间下多线程创建
            originUrl += UUID.randomUUID().toString();
            //该工具类只要内容不更改生成的数据仍是一样
            shortUri = HashUtil.hashToBase62(originUrl);
            //布隆过滤器不包含一定就不包含
            if (!shortLinkCreatCachePenetrationBloomFilter.contains(createDomainDefault + "/" + shortUri)) {
                break;
            }
            creatgenerateSuffix++;
        }
        return shortUri;
    }

    private void verificationWhitelist(String originUrl) {
        Boolean enable = gotoDomainWhiteListConfiguration.getEnable();
        if (enable == null || !enable) {
            return;
        }
        String domain = LinkUtil.extractDomain(originUrl);
        if (StrUtil.isBlank(domain)) {
            throw new ClientException("跳转链接填写错误");
        }
        List<String> details = gotoDomainWhiteListConfiguration.getDetails();
        if (!details.contains(domain)) {
            throw new ClientException("演示环境为避免恶意攻击，请生成以下网站跳转链接：" + gotoDomainWhiteListConfiguration.getNames());
        }
    }

}
