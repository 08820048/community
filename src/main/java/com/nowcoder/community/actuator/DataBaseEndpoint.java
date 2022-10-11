package com.nowcoder.community.actuator;

import com.nowcoder.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: Tisox
 * @date: 2022/4/9 15:47
 * @description:
 * @blog:www.waer.ltd
 */
@SuppressWarnings({"all"})
@Component
@Endpoint(id = "database")
public class DataBaseEndpoint {

    private Logger logger = LoggerFactory.getLogger(DataBaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    @ReadOperation
    public String checkConnect(){
        try
                (Connection conn = dataSource.getConnection()){
            return CommunityUtil.getJSONString(0,"获取连接成功!");
        }catch (SQLException e) {
            logger.error("获取连接失败:" + e.getMessage());
            return CommunityUtil.getJSONString(1,"获取连接失败!");
        }
    }

}
