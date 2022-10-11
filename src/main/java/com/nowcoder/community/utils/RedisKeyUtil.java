package com.nowcoder.community.utils;

/**
 * @author: Tisox
 * @date: 2022/1/28 15:57
 * @description:
 * @blog:www.waer.ltd
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";

    private static final String PREFIX_ENTITY_LIKE="like:entity";

    private static final String PREFIX_USER_LIKE = "like:user";
    /*目标：被关注者*/
    private static final String PREFIX_FOLLOWEE = "followee";
    /*粉丝：关注者*/
    private static final String PREFIX_FOLLOWER = "follower";
    /*用户登录凭证:验证码*/
    private static final String PREFIX_KAPTCHA = "kaptcha";
    /*用户凭证存储*/
    private static final String PREFIX_TICKET = "ticket";
    /*缓存用户信息*/
    private static final String PREFIX_USER = "user";
    /*统计UV*/
    private static final String PREFIX_UV ="uv";
    /*统计DAU*/
    private static final String PREFIX_DAU = "dau";
    /*记录帖子变动数据*/
    private static final String PREFIX_POST = "post";


    /**
     * 某个实体的赞
     *
     * @return String
     */
    public static String getEntityLikeKey(int entityType,int entityId){
        return  PREFIX_ENTITY_LIKE + entityType+SPLIT+entityId;
    }

    /**
     * 某个用户的赞
     * @param userId 用户id
     * @return String
     */
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT + userId;
    }

    /**
     * 某个用户关注的实体
     * @param userId 用户id
     * @param entityType 实体类型
     * @return String
     */
    //followee:userId:entityType->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某个实体拥有的粉丝
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return String
     */
    //folower:entityTyoe:entityId->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码
     * @return String
     */
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLIT+ owner;
    }

    /**
     * 登录凭证获取
     * @return string
     */
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return String
     */
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 获取单日UV信息
     * @param date 日期
     * @return 字符串
     */
    public static String getUVKey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 获取区间UV信息
     * @param startdate 开始日期
     * @param endDate 结束日期
     * @return 字符串
     */
    public static String getUVKey(String startdate,String endDate){
        return PREFIX_UV + SPLIT + startdate + SPLIT + endDate;
    }

    /**
     * 获取日DAU信息
     * @param date 日期
     * @return 字符串
     */
   public static String getDAUKey(String date){
        return PREFIX_DAU+ SPLIT + date;
   }

    /**
     * 获取区间DAU信息
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 字符串
     */
   public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+ SPLIT + startDate + SPLIT + endDate;
   }

    /**
     * 获取POST分数的key
     * @return String
     */
   public static String getPostScoreKey(){
       return PREFIX_POST+ SPLIT+"score";
   }

}
