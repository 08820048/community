package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * @author: XuYi
 * @date: 2021/11/21 11:23
 * @description:
 */
//@Configuration:表示这是一个配置类而非普通类
//配置类的方法名就是Bean的名称

@Configuration
public class AlphaConfog {
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
