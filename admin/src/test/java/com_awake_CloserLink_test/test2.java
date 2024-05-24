package com_awake_CloserLink_test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * @Author 清醒
 * @Date 2024/5/16 21:09
 */
public class test2 {
    public static String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }

    @Test
    public void test() {
        Date date = new Date();
        int hour = DateUtil.hour(date, true);
        int i = DateUtil.dayOfWeek(date);
        int week = DateUtil.weekOfMonth(date);
        System.out.println(week);
    }

    @Test
    public void test1() {

            String uv = UUID.fastUUID().toString();
            System.out.println(uv);

    }


    @Test
    public void test2() {
        HashMap<String, Object> localeStatsMap = new HashMap<>();
        localeStatsMap.put("ip","114.247.50.2");
        localeStatsMap.put("key","460c0075ca51d0ba13afb4e6deb14d32");
        String localeStatsAmap= HttpUtil.get("https://restapi.amap.com/v3/ip", localeStatsMap);
        JSONObject jsonObject = JSON.parseObject(localeStatsAmap);
        String infocode = jsonObject.getString("infocode");
        System.out.println(infocode);

    }

}


