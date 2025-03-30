package com.example.utils;


import io.jsonwebtoken.*;

import java.util.Date;
import java.util.UUID;

/**
 * JWT令牌生成与解析工具类
 */
public class JwtUtil {
    /**
     * JWT签名密钥（建议使用Base64编码的密钥）
     */
    private static final String SECRET_KEY = "d8c986df-8512-42b5-906f-eeea9b3acf86";
    /**
     * 令牌默认有效期：7天（单位：毫秒）
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * 核心方法，生成JWT令牌
     * <p>
     * 生成逻辑：
     * 1. 设置令牌头参数（类型：JWT，签名算法：HS256）
     * 2. 声明载荷信息（用户ID、角色）
     * 3. 设置标准字段（主题、过期时间、唯一ID）
     * 4. 使用HS256算法和密钥签名并返回字符串
     *
     * @param id   用户ID（必须为正整数）
     * @param role 用户角色标识（如：1-管理员，2-普通用户）
     * @return 生成的JWT令牌字符串
     */
    public static String generateToken(Integer id, Integer role) {
        JwtBuilder builder = Jwts.builder();

        // 1. 设置头信息
        builder.setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256");

        // 2. 设置声明
        builder.claim("id", id)
                .claim("role", role);

        // 3. 设置标准字段
        builder.setSubject("用户认证")
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .setId(UUID.randomUUID().toString());

        // 4. 签名并返回
        return builder.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT令牌并获取声明信息
     *
     * @param token 待解析的JWT令牌字符串
     * @return 解析后的声明对象（Claims），解析失败返回null
     */
    public static Claims parseToken(String token) {
        try {
            // 1. 创建解析器并设置密钥
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);

            // 2. 返回声明内容
            return jws.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // 令牌无效或已过期
            return null;
        }
    }
}
