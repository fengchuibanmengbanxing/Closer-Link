package com.awake.CloserLink.project.Mq.Config;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author 清醒
 * @Date 2024/5/30 22:36
 */
@Component
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ReturnsCallback, RabbitTemplate.ConfirmCallback {

    //  我们发送消息使用的是 private RabbitTemplate rabbitTemplate; 对象
    //  如果不做设置的话 当前的rabbitTemplate 与当前的配置类没有任何关系！
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //  设置 表示修饰一个非静态的void方法，在服务器加载Servlet的时候运行。并且只执行一次！
    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 表示消息是否正确发送到了交换机上
     * @param correlationData   消息的载体
     * @param ack   判断是否发送到交换机上
     * @param cause 原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if(ack){
            log.info("消息ID：" + correlationData.getId());
            log.info("消息发送成功！");
        }else {
            log.error("消息ID：" + correlationData.getId());
            log.error("消息发送失败！");
        }
    }

    /**
     * 消息如果没有正确发送到队列中，则会走这个方法！如果消息被正常处理，则这个方法不会走！
     * @param returnedMessage 返回消息封装体
     */

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        System.out.println("消息主体: " + new String(returnedMessage.getMessage().getBody()));
        System.out.println("应答码: " + returnedMessage.getReplyCode());
        System.out.println("描述：" + returnedMessage.getReplyText());
        System.out.println("消息使用的交换器 exchange : " + returnedMessage.getExchange());
        System.out.println("消息使用的路由键 routing : " + returnedMessage.getRoutingKey());
    }
}