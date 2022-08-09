package cn.zjk.npp.aspect;

import cn.zjk.npp.anotation.MySecurity;
import cn.zjk.npp.utils.AESUtil;
import cn.zjk.npp.utils.EncryptBean;
import cn.zjk.npp.utils.RSAUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.tomcat.util.codec.binary.Base64;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @description: TODO
 * @Author zjk
 * @className: MySecurityAspect
 * @date: 2022/8/9 15:49
 */

@Aspect
@Component
public class MySecurityAspect {
    private static final Logger log = LoggerFactory.getLogger(MySecurityAspect.class);

    @Value("${server.private-key}")
    private String PRIVATEKEY;
    @Value("${client.public-key}")
    private String PUBLICKEY;

    @Pointcut("@within(cn.zjk.npp.anotation.MySecurity) || @annotation(cn.zjk.npp.anotation.MySecurity)")
    public void point(){}

    @Around(value = "point()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes !=null;
            //request
            HttpServletRequest request = attributes.getRequest();
            //http请求方法
            String httpMethod = request.getMethod().toLowerCase();
            //Method
            Method method =((MethodSignature)proceedingJoinPoint.getSignature()).getMethod();

            Object[] args = proceedingJoinPoint.getArgs();

            MySecurity mySecurity = method.getAnnotation(MySecurity.class);
            if(mySecurity == null){
                mySecurity = proceedingJoinPoint.getClass().getAnnotation(MySecurity.class);
            }

            InputStreamReader inputStreamReader = new InputStreamReader(request.getInputStream(), "UTF-8");
            StringBuilder stringBuilder;
            try (BufferedReader br = new BufferedReader(inputStreamReader)) {
                String line;
                stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

            boolean hasDecrypt = mySecurity.decrypt();
            boolean hasEncrypt = mySecurity.encrypt();

            String oridata = stringBuilder.toString();
            JSONObject jsonObject = JSONObject.parseObject(oridata);
            String line = prettyJson(oridata);
            log.info("入参的切面json串:\r\n{}", line);

            if ("post".equals(httpMethod) && hasDecrypt) {
                String data = null;
                if (oridata.indexOf("data") != -1) {
                    data = jsonObject.getString("data");
                    jsonObject = JSONObject.parseObject(data);
                }
                //AES加密后的数据
                String aesKey = jsonObject.getString("aesData");
                //后端RSA公钥加密后的AES数据
                String dataString = jsonObject.getString("rsaData");
                //后端私钥解密的到AES的key
                byte[] plaintext = RSAUtil.decryptByPrivateKey(Base64.decodeBase64(dataString), PRIVATEKEY);

                String rsaData = new String(plaintext);
                log.info("解密出来的AES生成的key为:{}", rsaData);

                String originData = AESUtil.decrypt(aesKey, rsaData);
                log.info("解密出来的data数据:\r\n{}", prettyJson(originData));
                if (args.length > 0) {
                    args[0] = JSONObject.parseObject(originData, args[0].getClass());
                }
            }

            if (hasEncrypt) {
                String  aesKey = AESUtil.getKey();
                log.info("AES的key:{}", aesKey);

                String  dataString = jsonObject.toJSONString();
                String aesData = AESUtil.encrypt(dataString, aesKey);
                log.info("AesUtil加密后的数据:{}", aesData);

                String rsaData = Base64.encodeBase64String(RSAUtil.encryptByPublicKey(aesKey.getBytes(), PUBLICKEY));
                log.info("RsaUtil加密后的data数据:{}", rsaData);

                EncryptBean encryptBean = new EncryptBean();
                encryptBean.setAesData(aesData);
                encryptBean.setRsaData(rsaData);

                log.info("加密后的数据为:{}", JSONObject.toJSONString(encryptBean));
                if (args.length > 0) {
                    args[0] = JSONObject.parseObject(JSONObject.toJSONString(encryptBean), args[0].getClass());
                }
            }

            return proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            log.error(e.getMessage());
            return JSONObject.parseObject("{ \"加解密异常\": \"" +e.getMessage() + "\"}");
        }
    }

    public static String prettyJson(String reqJson) {
        JSONObject object = JSONObject.parseObject(reqJson);
        return JSON.toJSONString(object, new SerializerFeature[]{SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat});
    }
}
