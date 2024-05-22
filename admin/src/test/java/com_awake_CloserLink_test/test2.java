package com_awake_CloserLink_test;

import cn.hutool.core.date.DateUtil;
import org.junit.Test;

import java.util.Date;
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



}


