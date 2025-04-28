package com.example.aop;

import com.example.context.LocalThreadHolder;
import com.example.pojo.api.ApiResult;
import com.example.pojo.em.RoleEnum;
import com.example.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口权限校验切面
 * 1. 校验用户登录状态
 * 2. 校验接口访问权限
 */
@Aspect
@Component
public class ProtectorAspect {

    /**
     * 拦截带@Protector注解的接口
     */
    @Around("@annotation(com.example.aop.Protector)")
    public Object auth(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 从请求头获取token
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");

        // 未携带token直接返回错误
        if (token == null) {
            return ApiResult.error("身份认证失败，请先登录");
        }

        // 解析token获取用户信息
        Claims claims = JwtUtil.parseToken(token);
        if (claims == null) {
            return ApiResult.error("身份认证失败，请先登录");
        }
        Integer userId = claims.get("id", Integer.class);
        Integer roleId = claims.get("role", Integer.class);

        // 获取方法上的权限配置
        Protector protectorAnnotation = ((MethodSignature) proceedingJoinPoint.getSignature())
                .getMethod().getAnnotation(Protector.class);
        String requiredRole = protectorAnnotation.role();

        // 校验用户角色是否符合要求
        if (!requiredRole.isEmpty() && !requiredRole.equals(RoleEnum.ROLE(roleId))) {
            return ApiResult.error("无操作权限");
        }

        // 保存用户信息供后续使用
        LocalThreadHolder.setUserId(userId, roleId);

        try {
            // 执行原方法
            return proceedingJoinPoint.proceed();
        } finally {
            // 请求完成后清理用户数据
            LocalThreadHolder.clear();
        }
    }
}

