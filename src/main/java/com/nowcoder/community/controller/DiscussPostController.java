package com.nowcoder.community.controller;

import com.nowcoder.community.Event.EventProducer;
import com.nowcoder.community.entity.*;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import com.nowcoder.community.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author: Tisox
 * @date: 2022/1/14 17:27
 * @description: 处理帖子相关
 * @blog:www.waer.ltd
 */
@Controller
@SuppressWarnings({"all"})
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(Objects.equals(user,null)){
            return CommunityUtil.getJSONString(403,"你还没有登录哈！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //触发发帖事件
        Event event  = new Event().setTopic(TOP_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);
        /*计算帖子分数*/
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //计算贴子分数的时候，我们希望对同一个帖子的计算不是重复的
        //因此这里不能使用队列来存放这些帖子，要求无重复且无序，考虑set结构。
        redisTemplate.opsForSet().add(redisKey,post.getId());

        return CommunityUtil.getJSONString(0,"发布成功!");
    }

    /**
     * 查询帖子详情
     * @param discussPostId 帖子id
     * @param model model
     * @return String
     */
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        /*查询帖子*/
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        /*查询帖子作者*/
        User user = userService.findUserById(post.getUserId());
        /*点赞数*/
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,
                discussPostId);
        model.addAttribute("likeCount",likeCount);
        /*点赞状态*/
        int likeStatus =hostHolder.getUser()==null ? 0 :  likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,discussPostId);
        model.addAttribute("likeStatus",likeStatus);
        model.addAttribute("user",user);
        //查询评论的分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());
        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        //评论VO列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for(Comment comment : commentList){
                //评论VO
                Map<String,Object>  commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                /*点赞数*/
                 likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,
                        comment.getId());
                //model.addAttribute("likeCount",likeCount);
                commentVo.put("likeCount",likeCount);
                /*点赞状态*/
                 likeStatus =hostHolder.getUser()==null ? 0 :
                         likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
               // model.addAttribute("likeStatus",likeStatus);
                commentVo.put("likeStatus",likeStatus);

                //查询回复
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //回复的VO列表
                List<Map<String,Object>>  replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //处理回复的目标
                        User target = reply.getTargetId()==0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        /*点赞数*/
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,
                                reply.getId());
                        replyVo.put("likeCount",likeCount);
                        /*点赞状态*/
                        likeStatus =hostHolder.getUser()==null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,
                                        reply.getId());
                        replyVo.put("likeStatus",likeStatus);


                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }

    /**
     * 置顶操作
     * @param id 帖子id
     * @return String
     */
    @RequestMapping(path = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateType(id,1);
        //同步数据到elasticsearch服务器
        //触发发帖事件
        Event event  = new Event().setTopic(TOP_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 帖子加精
     * @param id
     * @return
     */
    @RequestMapping(path = "/wonderful",method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id,1);
        //同步数据到elasticsearch服务器
        //触发发帖事件
        Event event  = new Event().setTopic(TOP_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        /*计算帖子分数*/
        String redisKey = RedisKeyUtil.getPostScoreKey();
        //计算贴子分数的时候，我们希望对同一个帖子的计算不是重复的
        //因此这里不能使用队列来存放这些帖子，要求无重复且无序，考虑set结构。
        redisTemplate.opsForSet().add(redisKey,id);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 删除帖子
     * @param id
     * @return
     */
    @RequestMapping(path = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id,2);
        //同步数据状态到elasticsearch服务器
        //触发删帖事件
        Event event  = new Event().setTopic(TOP_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
