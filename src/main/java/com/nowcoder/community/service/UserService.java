package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;

/**
 * @author: XuYi
 * @date: 2021/11/21 18:25
 * @description:
 */
@Service
public class UserService  implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 用户注册功能
     * @param user 注册用户
     * @return map
     */
    public Map<String,Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        /*处理空值*/
        if(Objects.equals(user,null)){
            throw new IllegalArgumentException("参数不能为空!");

        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空！");
            return map;
        }
        /*验证账号*/
        User u = userMapper.selectByName(user.getUsername());
        if(!Objects.equals(u,null)){
            map.put("usernameMsg","该账号已存在!");
            return map;
        }
        /*验证邮箱*/
        u= userMapper.selectByEmail(user.getEmail());
        if(!Objects.equals(u,null)){
            map.put("emailMsg","该邮箱已被注册!");
            return map;
        }
        /*用户注册*/
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        /*发送激活邮件*/
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        /*http:localhost:8080/community/activation/101/code*/
        String url = domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String contents = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",contents);
        return map;
    }

    /**
     * 激活注册过的账号
     * @param userId 注册用户id
     * @param code 携带的激活码
     * @return 激活状态
     */
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);
        if(Objects.equals(user.getStatus(),1)){
            return ACTIVATION_REPEAT;
        }else if(Objects.equals(user.getActivationCode(),code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 根据id查找用户对象
     * @param id id
     * @return User
     */
    public User findUserById (int id){
        return userMapper.selectById(id);
    }

    /**
     * 处理登录业务
     * @param username 用户名
     * @param password 密码
     * @param expiredSeconds 过期时间
     * @return map
     */
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();
        /*空值处理*/
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        /*合法性验证*/
        User user = userMapper.selectByName(username);
        if(Objects.equals(user,null)){
            map.put("usernameMsg","该账号不存在!");
            return map;
        }
        /*账号是否激活*/
        if(user.getStatus()==0){
            map.put("usernameMsg","该账号未激活!");
            return map;
        }
        /*密码校验*/
        password = CommunityUtil.md5(password+user.getSalt());
        if(!Objects.equals(user.getPassword(),password)){
            map.put("passwordMsg","密码不正确!");
            return map;
        }
        /*生成登录凭证*/
        LoginTicket loginTicket  = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        /*过期时间为当前时间往后推移expiredSeconds*1000秒*/
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds * 1000L));
        loginTicketMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    /**
     * 退出登录
     * @param ticket 凭证
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    /**
     * 服务层根据用户凭证查询用户的信息
     * @param ticket ticket
     * @return loginTicket
     */
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    /**
     * 更新用户头像路径
     * @param userId 用户id
     * @param headerUrl 头像路径
     * @return int
     */
    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    public User findByUsername(String username){
        return userMapper.selectByName(username);
    }
}
