package com.nowcoder.community.utils;

/**
 * @author: XuYi
 * @date: 2021/11/24 19:22
 * @description: 公用数据接口
 */
public interface CommunityConstant {
    /*激活成功*/
    int ACTIVATION_SUCCESS = 0;
    /*重复激活*/
    int ACTIVATION_REPEAT = 1;
    /*激活失败*/
    int ACTIVATION_FAILURE = 2;
    /*默认状态登录凭证的超时时间:12小时*/
    int DEFAULT_EXPIRED_SECONDS = 60;
    /*记住我状态超时时间:100天*/
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
    /*实体类型：帖子*/
    int ENTITY_TYPE_POST=1;
    /*实体类型：评论*/
    int ENTITY_TYPE_COMMENT=2;
    /*实体类型:用户*/
    int ENTY_TYPE_USER = 3;
    /*主题：评论*/
    String TOPIC_COMMENT = "comment";
    /*主题:点赞*/
    String TOPIC_LIKE = "like";
    /*主题：关注*/
    String TOPIC_FOLLOW = "follow";
    /*系统用户ID*/
    Integer  SYSTEM_ID=1;

    /**
     * 主题：发帖
     */
    String TOP_PUBLISH = "publish";

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：普通用户
     */
    String AUTHORITY_MODERATOR = "moderator";

    /**
     * 主题：删帖
     */
    String TOP_DELETE = "delete";

    /**
     * 主题：分享
     */
    String TOPIC_SHARE = "share";
}
