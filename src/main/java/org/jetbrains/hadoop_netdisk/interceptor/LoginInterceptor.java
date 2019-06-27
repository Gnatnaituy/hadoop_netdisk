package org.jetbrains.hadoop_netdisk.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @auther hasaker
 * @create_date 2019-06-25 19:58
 * @description
 */
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 拦截没有登录的操作
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        if (request.getSession().getAttribute("currentUser") != null) {
            return true;
        }

        request.getSession().setAttribute("msg", "请先登录!");
        response.sendRedirect("/user/login");

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    }

    public void afterHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    }
}
