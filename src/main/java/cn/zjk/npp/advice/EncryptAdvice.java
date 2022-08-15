package cn.zjk.npp.advice;

import cn.zjk.npp.anotation.MySecurity;
import cn.zjk.npp.utils.AESUtils;
import cn.zjk.npp.utils.RSAUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @description: TODO
 * @Author zjk
 * @className: EncryptAdvice
 * @date: 2022/8/15 16:05
 */

@RestControllerAdvice
@Slf4j
public class EncryptAdvice implements ResponseBodyAdvice {

    @Value("${client.public-key}")
    private String publicKey;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        boolean encrypt =false;
        if (returnType.getMethod().isAnnotationPresent(MySecurity.class)){
            MySecurity mySecurity = returnType.getMethodAnnotation(MySecurity.class);
            assert mySecurity != null;
            encrypt = mySecurity.encrypt();
        }
        if (encrypt){
            log.info("对方法method :【" + returnType.getMethod().getName() + "】返回数据进行加密");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
                // 生成aes秘钥
                String aseKey = getRandomString(16);
                // rsa加密
                String encrypted = RSAUtils.encryptedDataOnJava(aseKey, publicKey);
                // aes加密
                String responseData = AESUtils.encrypt(result, aseKey);
                Map<String, String> map = new HashMap<>(2);
                map.put("encrypted", encrypted);
                map.put("responseData", responseData);
                return map;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(returnType.getMethod().getName()+"方法返回数据解密异常：{}", e.getMessage());
            }
        }
        return body;
    }

    /**
     * 创建指定位数的随机字符串
     *
     * @param length 表示生成字符串的长度
     * @return 字符串
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
