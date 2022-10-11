package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CookieUtil;
import com.nowcoder.community.utils.HostHolder;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;

/**
 * @author: Tisox
 * @date: 2022/1/10 21:27
 * @description:
 * @blog:www.waer.ltd
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if(!Objects.equals(ticket,null)){
            /*查询凭证信息*/
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            /*检擦凭证是否还有效*/
            if(!Objects.equals(loginTicket,null) && Objects.equals(loginTicket.getStatus(),0) && loginTicket.getExpired().after(new Date())){
               /*根据凭证查询用户*/
                User user = userService.findUserById(loginTicket.getUserId());
                /*在本次请求持有用户信息*/
                /* 由于在获取用户信息的时候，浏览器堆服务器时一对多的情况，需要考虑并发情况，不能简单的进行一个变量的存储
                *  线程隔离处理：封装为工具类
                * */
                hostHolder.setUser(user);
                /*构建用户认证的结果并存入SecurityContext,便于Security进行授权*/
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user,user.getPassword(),userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(!Objects.equals(user,null) && !Objects.equals(modelAndView,null)){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
        SecurityContextHolder.clearContext();
    }
}
