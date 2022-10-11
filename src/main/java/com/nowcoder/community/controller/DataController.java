package com.nowcoder.community.controller;

import com.nowcoder.community.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author: Tisox
 * @date: 2022/3/30 21:05
 * @description:
 * @blog:www.waer.ltd
 */
@Controller
public class DataController {
    @Autowired
    private DataService dataService;

    /**
     * 统计页面
     * @return String
     */
    @RequestMapping(path = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    /**
     * 统计UV
     * @param start 开始日期
     * @param end 结束日期
     * @param model model
     * @return String
     *
     */
    @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        //这里也可以用return "/site/admin/data";，效果是一样的，
        //如果使用下面这种方式，表示将该请求转发到另一个请求中继续处理，这里也就是将它转发给上面的页面请求中
        //页面请求再通过return "/site/admin/data";便可以返回到页面模板。
        //这也就是为什么我们再上面这个方法中需要添加两种请求方式，就是为了兼容该方法的POST请求。
        return "forward:/data";
    }


    /**
     * 统计DAU
     * @param start 开始日期
     * @param end 结束日期
     * @param model model
     * @return String
     *
     */
    @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        //这里也可以用return "/site/admin/data";，效果是一样的，
        //如果使用下面这种方式，表示将该请求转发到另一个请求中继续处理，这里也就是将它转发给上面的页面请求中
        //页面请求再通过return "/site/admin/data";便可以返回到页面模板。
        //这也就是为什么我们再上面这个方法中需要添加两种请求方式，就是为了兼容该方法的POST请求。
        return "forward:/data";
    }
}
