package cn.zjk.npp.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: RSAUtils
 * @Author zjk
 * @className: RSAUtils
 * @date: 2022/8/15 15:44
 */
public class RSAUtils {


    private static Logger log = LoggerFactory.getLogger(RSAUtils.class);
/** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** */
    /**
     * 获取公钥的key
     */
    private static final String  PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /** */
    /**
     * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
     */
    private static final int INITIALIZE_LENGTH = 1024;

    /** */
    /**
     * <p>
     * 生成密钥对(公钥和私钥)
     * </p>
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(INITIALIZE_LENGTH);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /** */
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /** */
    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /** */
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** */
    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** */
    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /** */
    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * java端公钥加密
     */
    public static String encryptedDataOnJava(String data, String PUBLICKEY) {
        try {
            data = Base64.encodeBase64String(encryptByPublicKey(data.getBytes(), PUBLICKEY));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }

    /**
     * java端私钥解密
     */
    public static String decryptDataOnJava(String data, String PRIVATEKEY) {
        String temp = "";
        try {
            byte[] rs = Base64.decodeBase64(data);
            temp = new String(RSAUtils.decryptByPrivateKey(rs, PRIVATEKEY), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static void main(String[] args) throws Exception {
//        Map<String, Object> map = genKeyPair();
//        String public_key = getPublicKey(map);
//        String private_key = getPrivateKey(map);
//        System.out.println(private_key);
//        System.out.println(public_key);
//        String data = encryptedDataOnJava("zzzzpppp", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDKv3KJco0GM4B+3Oyj2wqm/Cwq9Q8mOXdNqDtDb+FmaL9Y64xz0Lk3INBmB4lonZfwIHOepK8lZvT+HxHxB4+vSHslMFrhBQ9mNOAyQ8ciDa7Lp9S1CK7l5udwncK7jx7SK2X5ADz5FQqeufVefatSfBpIK/1dGmg3p7jVFLM0CwIDAQAB");
//        byte[] bytes = encryptByPublicKey("zzz".getBytes(), public_key);
//        System.out.println(new String(decryptByPrivateKey(bytes, private_key)));
//        String zzz = encryptedDataOnJava("zzz", public_key);
//        System.out.println(zzz);
//        System.out.println(decryptDataOnJava(zzz, private_key));
//        System.out.println(decryptDataOnJava("aWvK/nwh2wE+NMu9KTHauOm9MqhVKrepwLkIhGuhMvgfyNwPe0bOZ+RrcnT8Q1Nw/XkI/sHxZLvlqb98tIl1vaJbjFaY5nNsHuBRneHkxpootaBt7MZleDOVgAYAa/7XTTYhTmO3HNksstreZxsJDN7JxUu+tYoPs6N7VMy12iU=","MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJC9B4Z2Djw3daZyjYxHeZe6ynWN8koVOoXiBS1cW8ySTI3UYxG3d8VtAGEXkNwaQQPfMfqmu27dF+zkBGPWxxTYJ+sFM2l+MZwMyT6D3Fm/bPCSMULNimAOjvPuH3Quj87fEa30Aw2Jc2QQAzbN6ZXh0AzX4rfOn778+xNqnLeDAgMBAAECgYAOUVS0PWttE4Nk4sIT1WI22zh55n/ISk9OvSwL7635D6PZB+CA+Sg09HJsKFuhGkvCKUsVY3b5IY/jVC8G/VEWHNAGVqUafDmY3MU+c0RkiiIand/4DAH3jxTK9bU6UZQidTr1RMENP60IEoHW9qRdGuk3cwZAFBrKY7O/ItuDiQJBAO6wrOlNCsRX1qrV+JfNcH3RNxgkt/YQWs4Yz06KQvjaXmz8Gao+r0V5eKa1QG51dBhjnePHleNuhBwGuhj6EmUCQQCbPB+O8zzpT9YcaGzMKIHG+RVx5DBWHR9kENDFU2eDZyw8UykhHxJhbFNwz4sGl8yt937bjm7d2b6re4W4Co/HAkEA7RddihhW25UMzIDh/5e3Z6KLgVXnzWmNaS7HrRI1WA9AwCziKXM+kloIeD+OEqpkeV1qFjnILzM10sjzzZ+I6QJBAIzFlfzFOXx+aLT0QY1WQnHUmYzhmK/O0xOkaB45h0pTOnsuEsM1cS6l1HkdzH5bFELCiOjDnmhSuKS7bES8VysCQHj5LElPjs5B10+/YgwmhFZe/gbD9CgNPbX1C9vY4zGJFDmDHzDguSg41zHMZ2Ywuu2flsDVYWwMp4xOstud5cE="));
//        System.out.println(decryptDataOnJava("DspllFMq+/OlZW88SVkk+bTRuA5/XG7AHyC0b3xzsFd7wixYl7phRVDxgO+NUz09dDzKsRTAeWffmnE5VkflgXAwYCDiSJB0v16+fNQZ4UcdrI4jI6rFM/aP91G52E0KOuJx225CP2LJtx01UiJ2mwgGvCU3D/R4yAEY01YQ8zs=",
//                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMq/colyjQYzgH7c7KPbCqb8LCr1DyY5d02oO0Nv4WZov1jrjHPQuTcg0GYHiWidl/Agc56kryVm9P4fEfEHj69IeyUwWuEFD2Y04DJDxyINrsun1LUIruXm53CdwruPHtIrZfkAPPkVCp659V59q1J8Gkgr/V0aaDenuNUUszQLAgMBAAECgYAdRaLNwWInAQyORGtOBPP2w/XEhfkX9bgJ2D4mMGMEnB70QZ3Xosz67pvn/zKRjh8/pg/LWc+HwOOiRO4cNJFD5V2Pf++/UqX6lQ1hiNy0Z5l3C5lDY9yzD0AI4aPNrMy7YMuGtb7VaeF8jIH+Q5sqeXkO7vEqkbx4ACoRVkgrUQJBAPbiFQOj0Iude1YEsUinCTSNKgQUM3fRxX+daUy+5ee+g5DhZPWHvHsV6JWhI7XoJT+BUxqOfUa70UwAPolRPO8CQQDSPCGA/JAqhALsD8JlEY+dLgFjPjvXGQCyGGeygTqOCok4AtVD+E2giHo9sUerbG8uUyST5HicxtAuwLfeE/KlAkAp2i3BzjV+VZYhAO6k6FwxInUq99m5yD/44FCHiB0lajCXkP4yeW5EV3R0WTrajZ4y49OYvusR4KkcfzYaxUDVAkEAuMyyhrd74mN4ThjsP/tXykqOXbZu03ze564cOQTve7w3Mk0LOlwhoGNXULNs1HrUoFvlYzsJ6oW4qeAtv8C5IQJAZjvVWSnrAZ6ZnN3/FbP1Ou5hmWR2hwdOR2VCurE0beLv45JkE3fNej0TbyOXcgc0SNSFgIO/07f/TDIlFiDEaQ=="));
//
//        System.out.println(decryptDataOnJava("n2upXcrD+kpXoJTSvW84RMm2ABslLLtO38NDFNZZXYeN0KD5hizgXmlCgcMGKfwP9EYPKnzmu7CrTmlVUMhn3DwnmQDSgTX5Ic4WYUaDtmQ8JnTMBerKaivLTbzZu/WFwP7MVTG9QGyS++NsjegS6rYxNXw6vxdG5Dim8P29CAs=","MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKS6iJm63g2qaki9qnZ7aYsdPhOQ5KxRBnyKsfEQVoAvcayRjOteZjNmiUb70yTcLeG9SpMF/hi43FXpHEujiXGj21ql2S2bQXZ6q8bdkpZ2c57jLQOSlhCsv8vGWGdciMmA99aPtS1YpKSwyQS2sJBSP70TZ8E58aKwmgZaMoo7AgMBAAECgYBD8WgcODYUXuC9pfUkL1zl8ybCfTXEWRsazT39luaGsorpSZNdFS+ELdubOozpHCOUgzW6FHOskLrYeHCiRg6O9dOkIWO2TqIsQWJLYUR34CTTKCKNNRu6No4Qm5xdde7bDczMBohofWhIsD8E/yNyzBaZniWyd9yurAMrtqgb8QJBANziXxQ9bJTNQPJEC/wFOtfEeW14at8vCluaxeVv2p2EfhRknWBqNG7MXMgEpnWUdp8hmyMaOoOHmRi0mBEij0kCQQC+6rhpk+CY0VDSZNMjskN0XOQpCyxtMouEtZ5gmdHoPUUZ3DcKj83yckDNIAgqPlZVu9cE45Kcn9NR93wAHxljAkBIqJZKHCJ5mEMC7mtazYWbsF+ZKQFOxTSNCY6LeI3bPtgScT5rlsQEpmjmytDO4LVchzt3Aou49IPh4pYbHNkxAkB0MglBAKaOe76z/nde320ckjivHkTQxRWTFLKq2pOdxESdQ6EgRXuz1oPIFHnDtCL5lRR9vkAKIWANIBIRRNw/AkEAoMDjGllrUniDthx8Kl69jF3LrOCGrPYqNrsycAJSI7HZwTX4w/u7d/xMSqBZ1fwHQH7LcVEjG1cXI9M/2PEIrQ=="));
        String aa = "page=1&limit=8";
        log.info("param:" + aa);
        String dataEnc = encryptedDataOnJava(aa, "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkuoiZut4NqmpIvap2e2mLHT4TkOSsUQZ8irHxEFaAL3GskYzrXmYzZolG+9Mk3C3hvUqTBf4YuNxV6RxLo4lxo9tapdktm0F2eqvG3ZKWdnOe4y0DkpYQrL/LxlhnXIjJgPfWj7UtWKSksMkEtrCQUj+9E2fBOfGisJoGWjKKOwIDAQAB");
        log.info("param.encrypt:" + dataEnc);
        String dataDec = decryptDataOnJava(dataEnc, "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKS6iJm63g2qaki9qnZ7aYsdPhOQ5KxRBnyKsfEQVoAvcayRjOteZjNmiUb70yTcLeG9SpMF/hi43FXpHEujiXGj21ql2S2bQXZ6q8bdkpZ2c57jLQOSlhCsv8vGWGdciMmA99aPtS1YpKSwyQS2sJBSP70TZ8E58aKwmgZaMoo7AgMBAAECgYBD8WgcODYUXuC9pfUkL1zl8ybCfTXEWRsazT39luaGsorpSZNdFS+ELdubOozpHCOUgzW6FHOskLrYeHCiRg6O9dOkIWO2TqIsQWJLYUR34CTTKCKNNRu6No4Qm5xdde7bDczMBohofWhIsD8E/yNyzBaZniWyd9yurAMrtqgb8QJBANziXxQ9bJTNQPJEC/wFOtfEeW14at8vCluaxeVv2p2EfhRknWBqNG7MXMgEpnWUdp8hmyMaOoOHmRi0mBEij0kCQQC+6rhpk+CY0VDSZNMjskN0XOQpCyxtMouEtZ5gmdHoPUUZ3DcKj83yckDNIAgqPlZVu9cE45Kcn9NR93wAHxljAkBIqJZKHCJ5mEMC7mtazYWbsF+ZKQFOxTSNCY6LeI3bPtgScT5rlsQEpmjmytDO4LVchzt3Aou49IPh4pYbHNkxAkB0MglBAKaOe76z/nde320ckjivHkTQxRWTFLKq2pOdxESdQ6EgRXuz1oPIFHnDtCL5lRR9vkAKIWANIBIRRNw/AkEAoMDjGllrUniDthx8Kl69jF3LrOCGrPYqNrsycAJSI7HZwTX4w/u7d/xMSqBZ1fwHQH7LcVEjG1cXI9M/2PEIrQ==");
        log.info("param.decrypt:" + dataDec);
//        Map<String,String> param = new HashMap<>();
//        param.put("data",dataEnc);
//        HttpClientUtil.doGet("http://192.168.1.107:8082/api/v1/szzx/local_auth_list", param);
    }
}
