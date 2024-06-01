package com.awake.CloserLink.project.Mq.producer;

import com.awake.CloserLink.project.Dto.Biz.ShortLinkStatsRecordDTO;
import com.awake.CloserLink.project.Mq.Constant.MQConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Author 清醒
 * @Date 2024/5/30 14:57
 */
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(ShortLinkStatsRecordDTO statsRecord) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        //发送监控数据对象
        rabbitTemplate.convertAndSend(MQConstant.CONFIRM_EXCHANGE_NAME,
                MQConstant.CONFIRM_ROUTING_KEY,
                statsRecord,
                correlationData);
    }
}