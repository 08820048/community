package com.nowcoder.community.Event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: Tisox
 * @date: 2022/2/12 21:42
 * @description:
 * @blog:www.waer.ltd
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;
    //处理事件
    public void fireEvent(Event event){
        /*将事件发布到指定的主题*/
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }

}
