package com.nowcoder.community.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: XuYi
 * @date: 2021/11/20 16:42
 * @description:
 */
@Slf4j
@Controller
@RequestMapping("alpha")
public class AIphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String syahello(){
        log.info("Hello Spring Boot.");
        return "Hello Spring Boot.";
    }
}
