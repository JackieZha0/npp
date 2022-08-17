package cn.zjk.npp.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @description: AES工具包
 * @Author zjk
 * @className: AESUtils
 * @date: 2022/8/15 15:45
 */
public class AESUtils {

    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * 解密
     * @param data
     * @param aseKey
     * @return
     */
    public static String decrypt(String data, String aseKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(aseKey.getBytes(),"AES"));
        byte[] decode = Base64.getDecoder().decode(data);
        byte[] bytes = cipher.doFinal(decode);
        return new String(bytes,"UTF-8");
    }

    /**
     *  加密
     * @param result
     * @param aseKey
     * @return
     */
    public static String encrypt(String result, String aseKey) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(aseKey.getBytes(),"AES"));
        byte[] bytes = cipher.doFinal(result.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(bytes);
    }
}
