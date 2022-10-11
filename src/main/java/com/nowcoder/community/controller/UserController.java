package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author: Tisox
 * @date: 2022/1/11 17:01
 * @description:
 * @blog:www.waer.ltd
 */

@Component
@RequestMapping("/user")
public class UserController  implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService flowService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secreKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;


    /**
     * 返回页面并携带七牛云的服务token信息到页面处理
     * @param model model
     * @return String
     */
    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(Model model)
    {
        //上传文件名称
        String fileName =CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey, secreKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);
        return "/site/setting";
    }

    /**
     * 该方法已废弃
     * 上传头像
     * @param headerImage 头像
     * @param model model
     * @return Strng
     */
    @Deprecated
    @LoginRequired
    @RequestMapping(path = "upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(Objects.equals(headerImage,null)){
            model.addAttribute("error","请选择图片!");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf(".")+1);
        if(StringUtils.isEmpty(suffix)){
            model.addAttribute("error","文件格式不正确！");
        }
        /*生成随机的文件名*/
        filename = CommunityUtil.generateUUID()+suffix;
        /*确定文件存放的路径*/
        File dest = new File(uploadPath+"/"+filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!",e);
        }
        /*更新当前用户的头像路径*/
        //http://locahost:8080/community/user/header/xxxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/"+filename;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }


    /**
     * 该方法已废弃
     * 回写图片信息
     * @param fileName 文件名
     * @param response rseponse
     */
    @Deprecated
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        /*服务器存放路径*/
        fileName = uploadPath+"/"+fileName;
        /*文件后缀*/
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        /*响应图片*/
        response.setContentType("image/"+suffix);
        try(   FileInputStream fis = new FileInputStream(fileName);
               OutputStream os = response.getOutputStream();) {
            byte[] buffer = new byte[1024];
            int b  = 0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败:"+e.getMessage());
        }
    }

    /**
     * 个人主页
     * @param userId 用户id
     * @param model model
     * @return String
     */
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId")int userId,Model model){
        User user = userService.findUserById(userId);
        if(Objects.equals(user,null)){
            throw new RuntimeException("该用户不存在!");
        }
        /*用户*/
        model.addAttribute("user",user);
        /*点赞数量*/
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        /*查询关注数量*/
        long followeeCount = flowService.findFolloweeCount(userId, ENTY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        /*粉丝数量*/
        long followerCount = flowService.findFollowerCount(ENTY_TYPE_USER, userId);
        model.addAttribute("followerCount",followerCount);

        /*是否已关注*/
        boolean hasFollowed = false;
        if(hostHolder.getUser()!=null){
            hasFollowed = flowService.hasFollowed(hostHolder.getUser().getId(),ENTY_TYPE_USER,userId);
        }
        model.addAttribute("hasFollowed",hasFollowed);

        return "/site/profile";
    }

    /**
     * 返回用户头像的URL
     * @param fileName 头像文件名
     * @return JSON
     */
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String uploadHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1,"文件名不能为空!");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(),url);
        return CommunityUtil.getJSONString(0);
    }
}
