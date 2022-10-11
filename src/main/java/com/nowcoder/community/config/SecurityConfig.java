package com.nowcoder.community.config;

import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: Tisox
 * @date: 2022/3/28 20:49
 * @description: Security配置类
 * @blog:www.waer.ltd
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    /**
     *.ignoring() .antMatchers("/resources/**")：忽略指定资源路径
     *该路径下的资源访问将不受权限限制
     * @param web web
     * @throws Exception ex
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**");
    }

    /**
     * 执行授权
     * @param http http
     * @throws Exception ex
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/user/setting","/user/upload","/discuss/add","/comment/add/**","/letter/**","notice/**"
                        ,"like","/follow","unfollow")
                .hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                .antMatchers("/discuss/top","/discuss/wonderful")
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete","/data/**","/actuator/**")
                .hasAnyAuthority(AUTHORITY_ADMIN)

                .anyRequest().permitAll()
                        .and()
                                .csrf().disable();
        //权限不够时的处理
        http
                .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    /**
                     * 没有登录时候的处理,由于需要考虑异步请求返回的数据类型，我们不能直接使用html页面跳转的方式进行统一处理
                     * @param httpServletRequest hsr
                     * @param httpServletResponse hsrp
                     * @param e e
                     * @throws IOException ie
                     * @throws ServletException se
                     */
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        //判断请求类型:
                        //通过查看请求头[x-requested-with]返回的字段类型进行检查判断
                        //如果返回是[XMLHttpRequest]，说明该请求返回的内容是非html，通过[httpServletResponse]作一个回写操作提示
                        //一般返回状态码为403标识权限不足。
                        //否则直接进行重定向到登录页强制引导登录
                        String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"请登录后操作！"));
                        }else {
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    /**
                     * 权限不足的处理
                     * @param httpServletRequest httpServletRequest
                     * @param httpServletResponse httpServletResponse
                     * @param e E
                     * @throws IOException ie
                     * @throws ServletException se
                     */
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

                        String xRequestedWith = httpServletRequest.getHeader("x-requested-with");
                        if("XMLHttpRequest".equals(xRequestedWith)){
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJSONString(403,"权限不足！"));
                        }else {
                            //重定向到权限不足到404页面
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/denied");
                        }
                    }
                });
        /**
         * Security底层默认会拦截/logout请求进行处理
         * 覆盖它默认的逻辑，才能执行我们自己的退出代码
         * 这里通过覆盖一个不存在的退出的拦截路径，从而绕开它的默认拦截，走我们自己的退出逻辑
         */
        http
                .logout()
                .logoutUrl("/securitylogout");
    }
}
