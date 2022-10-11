package com.nowcoder.community;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author: Tisox
 * @date: 2022/1/28 10:16
 * @description:
 * @blog:www.waer.ltd
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings(){
        String redisKey  = "test:count";
        /*存*/
        redisTemplate.opsForValue().set(redisKey,1);
        /*取*/
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        /*加*/
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        /*减*/
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashTests(){
        String redisKey = "test:user";
        /*存*/
        redisTemplate.opsForHash().put(redisKey,"id",1);
        redisTemplate.opsForHash().put(redisKey,"username","Tisox");
        /*取*/
        System.out.println(redisTemplate.opsForHash().get(redisKey,"id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey,"username"));
    }

    @Test
    public void testLists(){
        String redisKey="test:ids";

        /*存*/
        redisTemplate.opsForList().leftPush(redisKey,101);
        redisTemplate.opsForList().leftPush(redisKey,102);
        redisTemplate.opsForList().leftPush(redisKey,103);
        /*取*/
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println( redisTemplate.opsForList().index(redisKey,0));
        System.out.println(redisTemplate.opsForList().range(redisKey,0,2));
        /*pop*/
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSets(){
        String redisKey="test:language";
        /*存*/
        redisTemplate.opsForSet().add(redisKey,"java","C++","python","甲骨文");

        /*取*/
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }

    @Test
    public void testSortedSets(){
        String redisKey="test:students";
        /*存*/
        redisTemplate.opsForZSet().add(redisKey,"王萌萌",80);
        redisTemplate.opsForZSet().add(redisKey,"李诗情",90);
        redisTemplate.opsForZSet().add(redisKey,"肖鹤云",78);
        redisTemplate.opsForZSet().add(redisKey,"张成",100);
        redisTemplate.opsForZSet().add(redisKey,"陶映红",60);

        /*取*/
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"肖鹤云"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"李诗情"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey,0,3));
        System.out.println(redisTemplate.opsForZSet().removeRange(redisKey,0,3));
    }

    @Test
    public void testKeys(){
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));

        /*设置过期时间：10秒*/
        redisTemplate.expire("test:students",10, TimeUnit.SECONDS);
    }

    @Test
    public void testHyperLogLog(){
        String redisKey = "test:hll:01";
        for(int i = 1;i<=100000;i++){
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        for(int i = 1;i<=100000;i++){
            int r = (int) (Math.random()*100000+1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println("****************************************的才能尽快立法的");
        System.out.println("***统计结果****"+size);
    }


    @Test
    public void testHyperLogLogUnion(){
        String key = "test:hll:02";
        for(int i = 1;i<=10000;i++){
            redisTemplate.opsForHyperLogLog().add(key,i);
        }
        String key1 = "test:hll:03";
        for(int i = 5001;i<=15000;i++){
            redisTemplate.opsForHyperLogLog().add(key1,i);
        }

        String key2 = "test:hll:04";
        for(int i =10001;i<=20000;i++){
            redisTemplate.opsForHyperLogLog().add(key2,i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey,key,key1,key2);
        Long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);
    }

    //统计一组数据的布尔值
    @Test
    public void testBitMap(){
        String redisKey = "test:bm:01";
        //再不同的位上记录不同的值
        System.out.println(redisTemplate.opsForValue().setBit(redisKey, 1, true));
        System.out.println(redisTemplate.opsForValue().setBit(redisKey, 4, true));
        System.out.println(redisTemplate.opsForValue().setBit(redisKey, 7, true));

        //统计
        Object  obj= redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

    }
}
