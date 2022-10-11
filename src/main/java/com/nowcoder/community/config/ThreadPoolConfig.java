package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: Tisox
 * @date: 2022/3/31 11:35
 * @description: Spring定时任务线程池配置类
 * @blog:www.waer.ltd
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

}
