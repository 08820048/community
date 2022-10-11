package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @author: Tisox
 * @date: 2022/1/16 21:48
 * @description:
 * @blog:www.waer.ltd
 */
@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType,int entityId,int offset,int limit);

    int selectCountByEntity(int entityType,int entityId);


    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
