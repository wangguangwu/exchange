package com.wangguangwu.exchange.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 * <p>
 * 提供获取客户端真实 IP 地址的工具方法，适配多种代理场景。
 *
 * @author wangguangwu
 */
public final class IpUtil {

    private static final String UNKNOWN = "unknown";

    /**
     * 获取客户端真实 IP 地址
     *
     * @param request HttpServletRequest 对象
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        // 从 X-Forwarded-For 中获取 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // X-Forwarded-For 可能包含多个 IP 地址，用逗号分隔，取第一个非空 IP
            return ip.split(",")[0].trim();
        }

        // 如果 X-Forwarded-For 不可用，则尝试其他代理头部
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (isValidIp(ip)) {
            return ip;
        }

        // 如果没有代理头部，直接从远程地址获取
        ip = request.getRemoteAddr();
        return ip != null ? ip.trim() : "";
    }

    /**
     * 校验 IP 是否有效
     *
     * @param ip IP 地址
     * @return 是否为有效的 IP 地址
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
