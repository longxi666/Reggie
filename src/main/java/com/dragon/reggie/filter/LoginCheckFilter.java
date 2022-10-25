package com.dragon.reggie.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *  TODO 创建过滤器检查用户是否完成登录
 */

@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器， 支持通配符
    public static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //TODO 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        //定义无需请求路径， 访问时直接放行
        String[] urls = new String[]{
                "/employee/login.html",
                "/employee/login.html",
                "/backend/**",
                "/front/**"
        };
        filterChain.doFilter(request,response);
        //TODO 2、判断本次请求是否需要处理

        //TODO 3、如果不需要处理，则直接放行
        //TODO 4、判断登录状态，如果已登录，则直接放行
        //TODO 5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据

    }

    /**
     * 进行路径匹配
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = pathMatcher.match(url,requestURI);
            if(match == true) return true;
        }
        return false;
    }
}
