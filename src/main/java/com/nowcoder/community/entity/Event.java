package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Tisox
 * @date: 2022/2/12 21:29
 * @description:
 * @blog:www.waer.ltd
 */
public class Event {
    /**
     * 主题
     */
    private String topic;
    /**
     * 用户id
     */
    private int userId;
    /**
     * 实体类型
     */
    private int entityType;
    /**
     * 实体id
     */
    private int entityId;
    /**
     * 实体作者
     */
    private int entityUserId;
    /**
     * 其他数据
     */
    private Map<String,Object> data  = new HashMap<>();

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }

    public Event() {
    }

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key,value);
        return this;
    }

    public Event(String topic, int userId, int entityType, int entityId, int entityUserId, Map<String, Object> data) {
        this.topic = topic;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityUserId = entityUserId;
        this.data = data;
    }
}
