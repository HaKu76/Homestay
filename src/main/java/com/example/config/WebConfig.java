package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域全局配置
 * 1. 允许所有域名访问
 * 2. 允许所有HTTP方法
 * 3. 允许所有请求头
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    // 配置跨域访问规则
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")    // 所有接口生效
                .allowedOrigins("*")  // 允许所有来源
                .allowedHeaders("*")  // 允许所有请求头
                .allowedMethods("*")  // 允许GET/POST等所有方法
                .maxAge(5000);        // 预检请求有效期（秒）
    }
}

