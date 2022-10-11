package com.nowcoder.community.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author: Tisox
 * @date: 2022/3/31 14:53
 * @description:
 * @blog:www.waer.ltd
 */
public class AlphaJob implements Job {

    /**
     *编写的定时器执行的任务
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(Thread.currentThread().getName()+":execute a quartz job.");
    }
}
