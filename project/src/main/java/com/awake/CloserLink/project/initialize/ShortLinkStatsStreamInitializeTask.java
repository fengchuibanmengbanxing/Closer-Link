package com.awake.CloserLink.project.initialize;

/**
 * @Author 清醒
 * @Date 2024/5/30 19:01
 */


/**
 * 实现InitializingBean接口后初始化bean时会自动调用afterPropertiesSet方法,
 * 使用redis自动创建消息队列主题与消费者组
 */
//@Component
//@RequiredArgsConstructor
//public class ShortLinkStatsStreamInitializeTask implements InitializingBean {
//
//    private final StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Boolean hasKey = stringRedisTemplate.hasKey(SHORT_LINK_STATS_STREAM_TOPIC_KEY);
//        if (hasKey == null || !hasKey) {
//            stringRedisTemplate.opsForStream().createGroup(SHORT_LINK_STATS_STREAM_TOPIC_KEY, SHORT_LINK_STATS_STREAM_GROUP_KEY);
//        }
//    }
//}