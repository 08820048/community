package com.nowcoder.community.service;

import org.springframework.context.annotation.Scope;
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
}
