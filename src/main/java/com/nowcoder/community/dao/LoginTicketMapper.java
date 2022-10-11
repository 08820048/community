package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * @author: Tisox
 * @date: 2022/1/9 15:12
 * @description:
 * @blog:www.waer.ltd
 */
@Mapper
@Deprecated//声明该组件不推荐使用了
public interface LoginTicketMapper {
    /**
     * 插入实现
     * @param loginTicket loginTicket实体对象
     * @return int
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired) ",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 根据用户的ticket作为条件查询用户信息
     * @param ticket 用户凭证
     * @return Ticket对象
     */
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 修改用户状态
     * @param ticket 凭证
     * @param status 状态
     * @return int
     */
    @Update({
            "<script>",
            "update login_ticket set status = #{status} where ticket=#{ticket} ",
            "<if test=\"ticket!=null\">",
            "and 1=1",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket,int status);

}
