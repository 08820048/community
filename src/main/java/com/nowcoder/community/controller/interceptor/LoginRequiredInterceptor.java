package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author: Tisox
 * @date: 2022/1/11 21:40
 * @description:
 * @blog:www.waer.ltd
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*判断拦截到的handler是否为HandlerMethod类型*/
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod =  (HandlerMethod) handler;
            /*获取其中的方法列表*/
            Method method = handlerMethod.getMethod();
            /*根据方法查询其带有的指定注解*/
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            /*逻辑判断:如果该方法有拦截的注解并且用户是未登录状态，就需要进行拦截*/
            if(!Objects.equals(loginRequired,null) && Objects.equals(hostHolder.getUser(),null)){
                /*重定向到登录页面*/
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
