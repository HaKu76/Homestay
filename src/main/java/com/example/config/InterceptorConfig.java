package com.example.config;

import com.example.Interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局拦截器配置
 * 1. 添加JWT验证拦截器
 * 2. 配置拦截规则
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    // 从配置application.yml读取接口基础路径
    @Value("${my-server.api-context-path}")
    private String API;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/**") // 拦截所有请求
                // 排除无需验证的接口（登录/注册/文件接口）
                .excludePathPatterns(
                        API + "/user/login",
                        API + "/user/register",
                        API + "/file/upload",
                        API + "/file/getFile"
                );
    }
}

