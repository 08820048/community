package com.nowcoder.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author: XuYi
 * @date: 2021/11/21 11:01
 * @description:
 */
@Repository
@Primary//该注解标识该bean具有优先级，会被优先执行
public class AlphaDaoMybatisImpl implements AlphaDao {
    @Override
    public String select() {
        return "MyBatis";
    }
}
