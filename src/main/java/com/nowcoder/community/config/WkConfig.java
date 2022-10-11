package com.nowcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author: Tisox
 * @date: 2022/4/1 18:04
 * @description: 生成长图的工具配置类
 * @blog:www.waer.ltd
 */
@Configuration
public class WkConfig {
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @PostConstruct
    public void init(){
    File file = new File(wkImageStorage);
    if(!file.exists()){
        file.mkdir();
        logger.info("创建WK目录:"+wkImageStorage);
        }
    }
}
