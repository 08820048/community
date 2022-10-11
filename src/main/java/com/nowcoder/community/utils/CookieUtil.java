package com.nowcoder.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author: Tisox
 * @date: 2022/1/10 21:30
 * @description:
 * @blog:www.waer.ltd
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request , String name){
        if (Objects.equals(request,null) || Objects.equals(name,null)){
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
