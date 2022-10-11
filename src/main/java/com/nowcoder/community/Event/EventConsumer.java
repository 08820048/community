package com.nowcoder.community.Event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import io.lettuce.core.ZAddArgs;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: Tisox
 * @date: 2022/2/12 21:59
 * @description:
 * @blog:www.waer.ltd
 */
@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Value("${wk.image.command}")
    private String wkImageCommand;
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secreKey;
    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;



    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(Objects.equals(record,null) || Objects.equals(record.value(),null)){
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }
        /*发送站内通知*/
        Message message = new Message();
        message.setFromId(SYSTEM_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    /**
     * 消费发帖事件
     * @param record
     */
    @KafkaListener(topics = {TOP_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record){
        if(Objects.equals(record,null) || Objects.equals(record.value(),null)){
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    /**
     * 消费删帖事件
     * @param record
     */
    @KafkaListener(topics = {TOP_DELETE})
    public void handleDeleteMessage(ConsumerRecord record){
        if(Objects.equals(record,null) || Objects.equals(record.value(),null)){
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    /**
     * 消费分享事件
     * @param record
     */
    @KafkaListener(topics = {TOPIC_SHARE})
    public void handleShareMessage(ConsumerRecord record){
        if(Objects.equals(record,null) || Objects.equals(record.value(),null)){
            logger.error("消息的内容为空！");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event==null){
            logger.error("消息格式错误！");
            return;
        }
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        //拼接命令
        String cmd = wkImageCommand + " --quality 75 "
                + htmlUrl + " " + wkImageStorage + "/" + fileName + suffix;
        try {
            Runtime.getRuntime().exec(cmd);
            logger.info("生成长图成功:" + cmd);
        } catch (IOException e) {
           logger.error("生成长图失败:" + e);
        }
        //启用定时器，监视该图片，一旦生成完毕将其上传到七牛云
        UploadTask task = new UploadTask(fileName, suffix);
        //5000ms
        //获取返回值future，可以用来停止该定时器
       Future future = taskScheduler.scheduleAtFixedRate(task, 500);
       task.setFuture(future);
    }

    class UploadTask implements Runnable {
        //文件名称
        private String fileName;
        //文件后缀
        private String suffix;
        //启动任务的返回值
        private Future future;
        //开始时间
        private long startTime;
        //上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            //初始化为系统当前时间
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
            //超时上传失败处理
            if(System.currentTimeMillis() - startTime >30000){
                logger.error("执行时间过长，终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            //超频上传失败处理
            if(uploadTimes>=3){
                logger.error("上传次数过多，终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            //记录图片本地路径
            String path = wkImageStorage + "/" + fileName + suffix;
            //判断是否存在该路径文件
            File file = new File(path);
            if(file.exists()){
                logger.info(String.format("开始第%d次上传[%s].",++uploadTimes,fileName));
                //设置响应信息
                StringMap policy = new StringMap();
                policy.put("returnBody", CommunityUtil.getJSONString(0));
                //生成上传凭证
                Auth auth = Auth.create(accessKey,secreKey);
                String uploadToken = auth.uploadToken(shareBucketName,fileName,3600,policy);
                //构建上传区域
                Configuration cfg = new Configuration(Region.huadongZheJiang2());
                //指定上传的机房
                UploadManager manager = new UploadManager(cfg);
                    try {
                        //开始上传图片
                        Response response = manager.put(path,fileName,uploadToken,null,"image/" + suffix,false);
                    //处理响应结果
                        JSONObject json = JSONObject.parseObject(response.bodyString());
                        if(json==null || json.get("code")==null || !json.get("code").toString().equals("0")){
                            logger.info(String.format("第%d次上传失败[%s].",uploadTimes,fileName));
                        }else{
                            logger.info(String.format("第%d次上传成功[%s].",uploadTimes,fileName));
                        }
                    }catch(QiniuException e) {
                        logger.info(String.format("第%d次上传失败[%s].",uploadTimes,fileName));
                    }
                }else {
                logger.info("等待图片生成["+ fileName +"].");
                }
            }
        }
    }
