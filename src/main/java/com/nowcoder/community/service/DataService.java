package com.nowcoder.community.service;

import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: Tisox
 * @date: 2022/3/30 17:53
 * @description:
 * @blog:www.waer.ltd
 */
@SuppressWarnings({"all"})
@Service
public class DataService {
    @SuppressWarnings({"all"})
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 格式化日期
     */
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 将指定IP计入UV
     * @param ip 用户IP
     */
    public void recordUV(String ip){
        String redisKey  = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }


    /**
     * 统计指定范围UV
     * @param start 开始日期
     * @param end 结束日期
     * @return LONG
     */
    public long calculateUV(Date start,Date end){
        //日期参数判断
        if(start == null || end==null){
            throw new IllegalArgumentException("日期参数不能为空！");
        }
        //整理日期范围内的key
        ArrayList<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //对结束日期的处理：时间小于等于end
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(key);
            //往后加一天
            calendar.add(Calendar.DATE,1);
        }
        //合并这些数据
        String redisKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());
        //返回统计结果
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    /**
     * 统计单日DUAU数据
     * @param userId 用户ID
     */
    public void recordDAU(int userId){
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);
    }

    /**
     * 统计区间DAU数据
     * @param start 开始日期
     * @param end 结束日期
     * @return 字符串
     */
    public long calculateDAU(Date start,Date end){
        //日期参数判断
        if(start == null || end==null){
            throw new IllegalArgumentException("日期参数不能为空！");
        }
        //整理日期范围内的key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        //对结束日期的处理：时间小于等于end
        while (!calendar.getTime().after(end)){
            String key = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(key.getBytes());
            //往后加一天
            calendar.add(Calendar.DATE,1);
        }
        //进行OR运算
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,redisKey.getBytes(),
                        keyList.toArray(new byte[0][0]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
    }
}
