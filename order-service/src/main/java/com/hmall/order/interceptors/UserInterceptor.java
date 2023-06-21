package com.hmall.order.interceptors;

import com.hmall.order.utils.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
@Component
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头
        String authorization  = request.getHeader("authorization");
        // 判断是否为空
        if (StringUtils.isBlank(authorization )) {
            throw new RuntimeException("未登录");
        }
        // 转换id
        Long userId = Long.valueOf(authorization );
        // 存入ThreadLocal
        UserHolder.setUser(userId);
        return true;
    }

    /**
     * 释放
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
