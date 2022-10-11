package com.nowcoder.community;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Tisox
 * @date: 2022/2/11 22:37
 * @description:
 * @blog:www.waer.ltd
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTests {
@Autowired
private KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        //阻塞主线程
    kafkaProducer.sendMessage("test","你好！");
    kafkaProducer.sendMessage("test","不良人！");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

/**
 * 封装生产者bean
 */
@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送消息
     * @param topic 主题
     * @param content 消息内容
     */
    public void sendMessage(String topic,String content){
        kafkaTemplate.send(topic, content);
    }
}

/**
 * 封装消费者bean
 */
@Component
class KafkaConsumer{
    //监听主题情况
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}