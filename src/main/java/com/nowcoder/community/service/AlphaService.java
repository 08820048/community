package com.nowcoder.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author: XuYi
 * @date: 2021/11/21 11:11
 * @description:
 */

//@Scope("prototype"):会创建多个实例

@Service
//@Scope("prototype")

public class AlphaService {
    private static Logger logger = LoggerFactory.getLogger(AlphaService.class);
    public AlphaService(){
        System.out.println("实例化AlphaService..");
    }

    //@PostConstruct：该注解标识在该方法会在构造器之后调用：初始化方法一般这样
    //@PreDestroy: 表示销毁对象之前调用方法

    @PostConstruct
    public void init(){
        System.out.println("初始化alphaService...");
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁AlphaService...");
    }

    /**
     * 让该方法在多线程的环境下被异步的调用
     */
    @Async
    public void execute1(){
        logger.debug("execute1");
    }

   //@Scheduled(initialDelay = 10000,fixedRate = 1000)
    public void execute2() {
        logger.debug("execute2");
    }
}
