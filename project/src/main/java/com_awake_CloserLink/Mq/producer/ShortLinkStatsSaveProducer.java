package com_awake_CloserLink.Mq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author 清醒
 * @Date 2024/5/30 14:57
 */
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.data.redis.channel-topic.short-link-stats}")
    private String topic;

    /**
     * 发送延迟消费短链接统计
     */
    public void send(Map<String, String> producerMap) {
        stringRedisTemplate.opsForStream().add(topic, producerMap);
    }
}