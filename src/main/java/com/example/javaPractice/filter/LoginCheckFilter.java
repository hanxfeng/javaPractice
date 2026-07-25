package com.example.javaPractice.filter;

import com.alibaba.fastjson.JSON;
import com.example.javaPractice.Config.BaseContext;
import com.example.javaPractice.Entity.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletRequest;

        String[] list = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 获取本次请求的 url
        String requestURI = httpServletRequest.getRequestURI();

        // 判断是否直接放行
        boolean check = check(list, requestURI);

        if (check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }

        // 管理端登录
        if (httpServletRequest.getSession().getAttribute("UserId") != null) {
            log.info("用户已登录，用户id为：{}",httpServletRequest.getSession().getAttribute("userId"));

            Long userId =(Long) httpServletRequest.getSession().getAttribute("UserId");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }

        // 移动端登录
        if (httpServletRequest.getSession().getAttribute("User") != null) {
            log.info("用户已登录，用户id为：{}",httpServletRequest.getSession().getAttribute("user"));
            Long userId =(Long) httpServletRequest.getSession().getAttribute("User");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(httpServletRequest, httpServletResponse);
            log.info("用户未登录");
            //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
            httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
