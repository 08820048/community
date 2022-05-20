package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author: XuYi
 * @date: 2021/11/21 17:04
 * @description:
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMap;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        System.out.println(user = userMapper.selectByName("liubei"));

        System.out.println(user = userMapper.selectByEmail("nowcoder101@sina.com"));
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        log.info(String.valueOf(rows));
//        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){
        System.out.println(userMapper.updateStatus(150, 1));
        System.out.println(userMapper.updateHeader(150, "http://www.nowcoder.com/102.png"));
        System.out.println(userMapper.updatePassword(150, "00000000"));
    }

    @Test
    public void testSelectPosts(){
       // List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(101,0,10,0);
        for (DiscussPost post : list) {
            System.out.println(post);
        }

//        int rows =discussPostMapper.selectDiscussPostRows(0);
//        System.out.println(rows);
    }

    /*测试登录模块的SQL*/
    @Test
    public void testInserLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("absa");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000 * 60 * 10));
        loginTicketMap.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket = loginTicketMap.selectByTicket("absa");
        System.out.println(loginTicket);

        loginTicketMap.updateStatus("absa",1);
         loginTicket = loginTicketMap.selectByTicket("absa");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for(Message message : list){
            System.out.println(message);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> list1 = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list1) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        int count1 = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count1);
    }
}
