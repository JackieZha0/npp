package cn.zjk.npp.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.Map;

/**
 * @description: jwt工具类
 * @Author zjk
 * @className: JwtUtil
 * @date: 2022/8/17 13:20
 */
public class JwtUtil {
    /**
     * 密钥
     */
    private static final String APP_SECRET = "qazwsx123456";

    public static final long EXPIRES = 1000 * 60 * 60 * 24;

    /**
     * 生成token
     * @param payload
     * @return
     */
    public static String generateToken(Map<String,String> payload){
        JWTCreator.Builder builder= JWT.create();
        payload.forEach(builder::withClaim);
        //签发对象
        return builder.withAudience("admin")
                // 发行时间
                .withIssuedAt(new Date())
                //过期时间
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES))
                //加密
                .sign(Algorithm.HMAC256(APP_SECRET));
    }

    /**
     * 校验token
     * @param token
     * @return
     */
    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(APP_SECRET)).build().verify(token);
    }

    /**
     * 根据token获取荷载信息
     * @param token
     * @return
     */
    public static Map<String, Claim> getPayloadByToken(String token){
        return verify(token).getClaims();
    }

}
