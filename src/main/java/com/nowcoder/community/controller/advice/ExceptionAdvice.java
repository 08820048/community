package com.nowcoder.community.controller.advice;

import com.nowcoder.community.utils.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * @author: Tisox
 * @date: 2022/1/27 11:44
 * @description:
 * @blog:www.waer.ltd
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler({Exception.class})
    public  void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常："+e.getMessage());
        /*遍历异常的详细信息*/
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }
        /*通过request来判断请求的类型：是否为异步请求*/
        String xRequestedWith = request.getHeader("x-requested-with");
        if(Objects.equals(xRequestedWith,"XMLHttprequest")){
            /*返回：plain:普通的字符串，人为处理成json*/
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常！"));
        }else{
            //普通请求：非异步
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
