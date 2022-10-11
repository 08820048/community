package com.nowcoder.community.utils;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author: Tisox
 * @date: 2022/1/10 21:51
 * @description: 持有用户信息，用于代替session对象。
 * @blog:www.waer.ltd
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    /**
     * 获设置用户信息
     * @param user 用户
     */
    public void setUser(User user){
        users.set(user);
    }

    /**
     * 获取用户信息
     * @return 用户
     */
    public User getUser(){
       return  users.get();
    }

    /**
     * 清理
     */
    public void clear(){
        users.remove();
    }
}
