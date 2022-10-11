package com.nowcoder.community;

import com.nowcoder.community.utils.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Tisox
 * @date: 2022/1/12 15:05
 * @description:
 * @blog:www.waer.ltd
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "这里可以赌博，我草泥马，来吸毒啊，开票啦！@吸毒";
         text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
