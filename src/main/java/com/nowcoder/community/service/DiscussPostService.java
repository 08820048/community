package com.nowcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.utils.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: XuYi
 * @date: 2021/11/21 18:20
 * @description:
 */
@SuppressWarnings({"all"})
@Service
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);
    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    //caffeine核心接口:Cache,常用子接口：LoadingCache,AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;
    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    //初始化缓存
    @PostConstruct
    public void init() {
        //初始化帖子列表
        postListCache = Caffeine.newBuilder()
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String key) throws Exception {
                        if(key == null || key.length()==0){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String [] params = key.split(":");
                        if(params==null || params.length!=2){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);
                        //这里可以再加一个二级缓存：redis

                        //访问数据库
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0,offset,limit,1);
                    }
                });
        //初始化帖子总数
        postRowsCache = Caffeine.newBuilder()
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .maximumSize(maxSize)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });

    }





    /**
     * 查询帖子信息
     * @param userId 用户id
     * @param offset 当前页的起始行
     * @param limit 总页数
     * @return list
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit,int orderMode) {
        //敲黑板：我们主要作的是对首页热门贴的缓存，所以只有当userId=0且orderMode=1的时候才启用缓存。
        //以offset和limit为key，获取缓存中的数据
        if(userId==0 && orderMode==1){
            return postListCache.get(offset + ":" + limit);
        }
        //记个日志
        logger.debug("load post list  from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    /**
     * 根据用户id查询帖子
     * @param userId 用户id
     * @return 查询结果
     */
    public int findDiscussPostRows(int userId){
        //也是在userId=0时触发，但是缓存的方法需要一个key作参数，所以这里来就用这个userId无碍。
        if(userId==0){
            return postRowsCache.get(userId);
        }
        //记个日志
        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * t添加帖子
     * @param post 帖子
     * @return int
     */
    public int addDiscussPost(DiscussPost post){
        /*参数判断*/
        if(post==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        /*转义HTML标记*/
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        /*敏感词过滤*/
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);

    }

    /**
     * 处理帖子详情
     * @param id 帖子id
     * @return 帖子对象实体
     */
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 查询帖子的评论数量
     * @param id 帖子id
     * @param commentCount 评论数量
     * @return int
     */
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    /**
     * 修改帖子类型
     * @param id 帖子id
     * @param type 帖子类型
     * @return int
     */
    public int updateType(int id,int type){
        return discussPostMapper.updateType(id, type);
    }

    /**
     * 修改贴子状态
     * @param id id
     * @param status status
     * @return int
     */
    public int updateStatus(int id,int status){
        return discussPostMapper.updateStatus(id, status);
    }

    /**
     * 更新帖子分数
     * @param id 帖子id
     * @param score 帖子分数
     * @return int
     */
    public int updateScore(int id,double score){
        return discussPostMapper.updateScore(id, score);
    }
}
