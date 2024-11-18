package com.wangguangwu.exchangeusercore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 生产环境安全配置
 *
 * @author wangguangwu
 */
@Configuration
@Profile("prod")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 配置授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许匿名访问的路径
                        .requestMatchers("/public/**").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                // 启用 HTTP Basic 认证
                // TODO：后续接入页面时，需要配置为表单登录
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
