package com.wangguangwu.exchangeusercore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * - 全局跨域配置类
 * - <p>
 * - 通过实现 {@link WebMvcConfigurer} 接口，为整个应用提供统一的跨域支持。
 * - 适用于前后端分离的项目。
 * <p>
 * - @author wangguangwu
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * - 配置跨域映射规则。
     * <p>
     * - @param registry 跨域注册器，用于定义跨域规则。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 匹配所有路径
        registry.addMapping("/**")
                // 允许跨域的源，可根据需求替换
                .allowedOrigins(
                        "http://localhost:5173"
                )
                // 允许的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头，* 表示支持所有头
                .allowedHeaders("*")
                // 允许发送 Cookie 和凭据
                .allowCredentials(true)
                // 设置预检请求的有效时间，单位为秒
                .maxAge(3600);
    }
}

