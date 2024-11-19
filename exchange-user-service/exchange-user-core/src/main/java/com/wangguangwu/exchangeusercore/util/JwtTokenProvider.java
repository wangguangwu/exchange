package com.wangguangwu.exchangeusercore.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
}