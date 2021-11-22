package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

/**
 * @author: XuYi
 * @date: 2021/11/21 10:54
 * @description:
 */
//每个bean都有一个默认的名字：类名首字母小写
//也可以为它进行命名：@Repository("alphaHibernate")
//这样就可以通过该名称调用对应的bean了

@Repository("alphaHibernate")
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
