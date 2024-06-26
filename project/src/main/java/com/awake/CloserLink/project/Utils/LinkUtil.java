package com.awake.CloserLink.project.Utils;

/**
 * @Author 清醒
 * @Date 2024/5/21 10:00
 */

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.Date;
import java.util.Optional;

/**
 * 短链接有效期
 */
public class LinkUtil {

    /**
     * @param valid 用户规定有效期时间
     * @return 有效期
     */
    public static long getLinkCacheValidTime(Date valid) {

        return Optional.ofNullable(valid)
                .map(v -> DateUtil.between(new Date(), v, DateUnit.MS))
                .orElse(2419200000L);
    }

    /**
     * 获取ip
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    /**
     * 获取操作系统
     */

    public static String getOS(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
            // 这里只是一个简化的示例，实际解析可能需要更复杂的逻辑来应对各种User-Agent格式
            if (userAgent.contains("Windows")) {
                return "Windows";
            } else if (userAgent.contains("Mac OS")) {
                return "Mac OS";
            } else if (userAgent.contains("Linux")) {
                return "Linux";
            } else if (userAgent.contains("Android")) {
                return "Android";
            } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
                return "iOS";
            } else {
                return "Unknown";
            }
    }
    /**
     * 浏览器
     */

    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 这里只是一个简化的示例，实际解析可能需要更复杂的逻辑来应对各种User-Agent格式
            // 简单示例，实际可能需要更复杂的逻辑
            if (userAgent.contains("Edg")) {
                return "Microsoft Edge";
            } else if (userAgent.contains("Firefox")) {
                return "Mozilla Firefox";
            } else if (userAgent.contains("Safari")) {
                return "Apple Safari";
            } else if (userAgent.contains("Chrome")) {
                return "Google Chrome";
            } else if (userAgent.contains("Trident") || userAgent.contains("MSIE")) { // IE11及更低版本
                return "Internet Explorer";
            } else {
                return "Unknown Browser";
            }

    }

    /**
     * 获取设备类型
     */
    public static String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        // 这里可以使用正则表达式或者第三方库来解析User-Agent字符串
        // 为了简化，这里仅做示意，实际应用中可能需要更复杂的解析逻辑
        if (userAgent.contains("Mobile")) {
            return "移动设备";
        } else if (userAgent.contains("Windows")) {
            return "Windows PC";
        } else if (userAgent.contains("Macintosh")) {
            return "Mac";
        } else {
            return "未知设备";
        }
    }

    /**
     * 获取用户访问网络
     */
    public static String getNetwork(HttpServletRequest request) {
        String actualIp = getIp(request);
        // 这里简单判断IP地址范围，您可能需要更复杂的逻辑
        // 例如，通过调用IP地址库或调用第三方服务来判断网络类型
        return actualIp.startsWith("192.168.") || actualIp.startsWith("10.")||actualIp.startsWith("127.0.") ? "WIFI" : "Mobile";
    }

    /**
     * 获取原始链接中的域名
     * 如果原始链接包含 www 开头的话需要去掉
     *
     * @param url 创建或者修改短链接的原始链接
     * @return 原始链接中的域名
     */
    public static String extractDomain(String url) {
        String domain = null;
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (StrUtil.isNotBlank(host)) {
                domain = host;
                if (domain.startsWith("www.")) {
                    domain = host.substring(4);
                }
            }
        } catch (Exception ignored) {
        }
        return domain;
    }
}
