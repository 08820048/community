package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Tisox
 * @date: 2022/1/24 15:33
 * @description:
 * @blog:www.waer.ltd
 */
@Mapper
public interface MessageMapper {
    /**
     * 查询当前用户的会话列表，针对每一个会话只返回一条最新的私信。
     * @param userId 用户id
     * @param offset 分页信息
     * @param limit 分页信息
     * @return List<Message>
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     * @param userId 用户id
     * @return int
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId 会话id
     * @param offset 分页支持
     * @param limit 分页支持
     * @return List<Message>
     */
    List<Message> selectLetters(String conversationId,int offset,int limit);

    /**
     * 查询某个会话所包含的私信数量
     * @param conversationId 会话id
     * @return int
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId 用户id
     * @param conversationId 会话ID
     * @return int
     */
    int selectLetterUnreadCount(int userId,String conversationId);

    /**
     * 添加私信(发送私信功能)
     * @param message 私信
     * @return int
     */
    int insertMessage(Message message);


    /**
     * 修改消息状态
     * @param ids 消息id
     * @param status 状态
     * @return int
     */
    int updateStatus(List<Integer> ids,int status);

    /**
     * 查询某个主题下最新的通知
     * @param userId 用户id
     * @param topic 主题
     * @return message
     */
    Message selectLatestNotice(int userId,String topic);

    /**
     * 查询某个主题包含的通知数量
     * @param userId 用户id
     * @param topic 主题哦
     * @return int
     */
    int selectNoticeCount(int userId,String topic);

    /**
     *未读的通知数量
     * @param userId 用户id
     * @param topic 主题
     * @return int
     */
    int selectNoticeUnreadCount(int userId,String topic);

    /**
     * c查询某个主题所包含的通知列表
     * @param userId 用户id
     * @param topic 主题
     * @param offset 分页支持
     * @param limit 分页支持
     * @return List
     */
    List<Message> selectNotices(int userId,String topic,int offset,int limit);


}
