package com.wangguangwu.exchangeusercore.util;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * @author wangguangwu
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;

    private Key key;

    /**
     * 初始化密钥
     */
    @PostConstruct
    protected void init() {
        // 使用 Base64 解码密钥字符串
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        // 将解码后的密钥转换为 HMAC-SHA256 所需的 Key 对象
        this.key = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 生成 JWT Token
     *
     * @param username 用户名
     * @return JWT Token
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证和解析 JWT Token
     *
     * @param token JWT Token
     * @return 解析后的 Claims 对象，包含 Token 的有效信息
     * @throws JwtException 如果 Token 无效或解析失败
     */
    public Claims validateToken(String token) {
        try {
            // 解析 Token
            return Jwts.parserBuilder()
                    // 设置密钥
                    .setSigningKey(key)
                    .build()
                    // 验证签名并解析
                    .parseClaimsJws(token)
                    // 获取解析后的 Claims 对象
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token 已过期", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token 无效", e);
        }
    }

    /**
     * 判断 Token 是否过期
     *
     * @param claims 已解析的 Token Claims
     * @return true 表示已过期，false 表示未过期
     */
    public boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }
}