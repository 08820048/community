package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author: XuYi
 * @date: 2021/11/21 18:20
 * @description:
 */
@Service
public class DiscussPostService {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /**
     * 查询帖子信息
     * @param userId 用户id
     * @param offset 当前页的起始行
     * @param limit 总页数
     * @return list
     */
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 根据用户id查询帖子
     * @param userId 用户id
     * @return 查询结果
     */
    public int findDiscussPostRows(int userId){
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
}
