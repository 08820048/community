package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: XuYi
 * @date: 2021/11/21 18:25
 * @description:
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById (int id){
        return userMapper.selectById(id);
    }
}
