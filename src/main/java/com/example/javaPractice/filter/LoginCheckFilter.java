package com.example.javaPractice.filter;

import com.alibaba.fastjson.JSON;
import com.example.javaPractice.Entity.Result;
import com.example.javaPractice.Config.BaseContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/*
检查用户是否已经完成登陆
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public  static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}",request.getRequestURI());

        // 获取本次请求的 url
        String url = request.getRequestURI();
        // 定义不需要处理的 url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        // 判断本次请求是否需要处理
        boolean check =  check(urls,request.getRequestURI());

        // 如果 check 返回 true,不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",request.getRequestURI());
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        // 如果返回 false 判断是否登陆，如果已登陆，直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登陆");
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        // 如果未登陆，则返回未登录结果，具体写法根据前端来定
        // 这里通过输出流的方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;

    }

    /**
     * 检查本次请求是否需要放行
     * @param urls
     * @param requestUrl
     * @return
     */
    public boolean check(String[] urls, String requestUrl) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
