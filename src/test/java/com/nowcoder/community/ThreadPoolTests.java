package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: Tisox
 * @date: 2022/3/31 10:21
 * @description: 演示线程池
 * @blog:www.waer.ltd
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ThreadPoolTests {

    private static Logger logger = LoggerFactory.getLogger(ThreadPoolTests.class);
    @Autowired
  private   AlphaService alphaService;
    //演示JDK普通线程池
    //初始化5个线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //注入Spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //注入定时任务线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 将当前线程休眠
     * @param m 休眠时间单位：秒
     */
    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试JDK普通线程池
     */
    @Test
    public void testExecutorService(){
        //通过【Runnable】创建一个线程体
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello ExecutorService!");
            }
        };
        for(int i=0;i<10;i++){
            executorService.submit(task);
        }
        sleep(10000);
    }


    /**
     * JDK定时任务线程池
     */
    @Test
    public void testScheduledExecutorService(){
        Runnable task = new Runnable(){
            @Override
            public void run() {
                logger.debug("hello ScheduledExecutorService!");
            }
        };
        //参数分别是任务、任务延迟时间10秒、时间间隔1000、单位毫秒
        scheduledExecutorService.scheduleAtFixedRate(task,10000,1000, TimeUnit.MILLISECONDS);
        //阻塞30s
        sleep(30000);
    }


    /**
     * Spring基本线程池测试
     */
    @Test
    public void testThreadPoolTaskExecutor(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("hello Spring基本线程池！");
            }
        };
        for(int i = 0;i<10;i++){
            threadPoolTaskExecutor.submit(task);
        }
    }

    /**
     * Spring定时任务线程池测试
     */
    @Test
    public void testThreadPoolTaskScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
               logger.debug("hello Spring定时任务线程池！");
            }
        };
        Date startTime = new Date(System.currentTimeMillis()+10000);
        threadPoolTaskScheduler.scheduleAtFixedRate(task,startTime,1000);
            sleep(30000);
    }

    //简化版
    @Test
    public void testtestThreadPoolTaskExecutorSimple(){
        for(int i=0;i<10;i++){
            alphaService.execute1();
        }
        sleep(30000);
    }

    @Test
    public void testtestThreadPoolTaskSchedulerSimple(){
        sleep(30000);
    }
}
