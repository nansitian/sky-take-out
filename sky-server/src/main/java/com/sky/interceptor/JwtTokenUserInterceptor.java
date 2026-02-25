package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;

/**
 * JWT令牌校验
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    JwtProperties jwtProperties;


    /**
     * 校验jwt
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("当前线程的id: " + Thread.currentThread().getId());

        //判断当前拦截到的是controller方法还是其他
        if(!(handler instanceof HandlerMethod)){
            //拦截到的不是动态资源, 直接放行
            return true;
        }

        //1. 从请求头中取出令牌
        String jwt = request.getHeader(jwtProperties.getUserTokenName());

        //2. 校验令牌
        try {
            log.info("校验令牌jwt: {}", jwt);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), jwt);
            //将从令牌中解析出来的id存入到当前线程的局部变量当中
            Long id = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(id);
            log.info("当前用户id {}", id);
            //3.通过放行
            return true;
        } catch (Exception e) {
            //4.不通过响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}
