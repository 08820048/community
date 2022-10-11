package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: Tisox
 * @date: 2022/2/25 9:50
 * @description:
 * @blog:www.waer.ltd
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class getSpringBootVersion {
    @Test
    public void getVersion(){
        String SpVersion = SpringVersion.getVersion();
        String SpBootVersion = SpringBootVersion.getVersion();
        System.out.println("_______________________________");
        System.out.println("spring版本："+SpVersion);
        System.out.println("SpringBoot版本："+SpBootVersion);
        System.out.println("___________________________________");
    }
}
