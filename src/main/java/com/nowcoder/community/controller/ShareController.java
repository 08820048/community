package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Tisox
 * @date: 2022/4/1 21:41
 * @description: 管理分享功能请求
 * @blog:www.waer.ltd
 */
@Controller
public class ShareController implements CommunityConstant {
    /**
     * 引入日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);
    /**
     * 生产者
     */
    @Autowired
    private EventProducer eventProducer;
    /**
     * 项目域名
     */
    @Value("${community.path.domain}")
    private String domain;

    /**
     * 项目访问路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 图片路径
     */
    @Value("${wk.image.storage}")
    private String wkImageStorage;

    @Value("${qiniu.bucket.share.name}")
    private String shareBucketName;

    @Value("${qiniu.bucket.share.url}")
    private String sharerBucketUrl;

    @RequestMapping(path = "/share",method = RequestMethod.GET)
    @ResponseBody
    public String share(String htmlUrl){
        //随机文件名
        String fileName = CommunityUtil.generateUUID();
        System.out.println("--------ShareController中的[flieName]------" + fileName);
        //异步生成长图
        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl",htmlUrl)
                .setData("fileName",fileName)
                .setData("suffix",".png");
        eventProducer.fireEvent(event);
        //返回图片的访问路径
        Map<String,Object> map = new HashMap<>();
       // map.put("shareUrl",domain + contextPath + "/share/image/" + fileName);
        map.put("shareUrl" , sharerBucketUrl + "/" + fileName);
        return CommunityUtil.getJSONString(0,null,map);
    }

    /**
     * 废弃
     * 获取长图
     * @param fileName 文件吗
     * @param response resp
     */
    @Deprecated
    @RequestMapping(path = "/share/image/{fileName}",method = RequestMethod.GET)
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response){
        if(StringUtils.isBlank(fileName)){
            throw new IllegalArgumentException("文件名不能为空!");
        }
        //指定输出的文件类型和格式
        response.setContentType("image/png");
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis  = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("获取长图失败:" + e.getMessage());
        }
    }
}
