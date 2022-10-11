package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author: Tisox
 * @date: 2022/4/9 8:40
 * @description:
 * @blog:www.waer.ltd
 */
@SuppressWarnings({"all"})
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class InitDataForTest {
    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest(){
        System.out.println("开始插入数据："+System.currentTimeMillis());
        for (int i = 0; i < 300000;i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("咖啡因缓存测试");
            post.setContent("压力测试-咖啡因缓存测试咖啡因缓存测试咖啡因缓存测试咖啡因缓存测试咖啡因缓存测试咖啡因缓存测试");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            postService.addDiscussPost(post);
        }
        System.out.println("数据插入结束："+System.currentTimeMillis());
    }

    @Test
    public void testCache(){
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        //不走缓存
        System.out.println(postService.findDiscussPosts(0, 0, 10, 0));
    }
}
