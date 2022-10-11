package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author: Tisox
 * @date: 2022/1/28 10:08
 * @description: 编写redis配置类
 * @blog:www.waer.ltd
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        /*实例化*/
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        /*设置key的序列化方式*/
        template.setKeySerializer(RedisSerializer.string());
        /*设置value的序列化方式*/
        template.setValueSerializer(RedisSerializer.json());
        /*设置哈希的key的序列化方式*/
        template.setHashKeySerializer(RedisSerializer.string());
        /*设置哈希的value的序列化方式*/
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
