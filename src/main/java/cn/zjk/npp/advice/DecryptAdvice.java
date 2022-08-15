package cn.zjk.npp.advice;

import cn.zjk.npp.anotation.MySecurity;
import cn.zjk.npp.utils.AESUtils;
import cn.zjk.npp.utils.RSAUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @description: 解密通知
 * @Author zjk
 * @className: DecryptAdvice
 * @date: 2022/8/15 14:41
 */

@RestControllerAdvice
@Slf4j
public class DecryptAdvice implements RequestBodyAdvice {

    @Value("${server.private-key}")
    private String privateKey;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        try {
            boolean encrypt = false;
            if(parameter.getMethod().isAnnotationPresent(MySecurity.class)){
                MySecurity mySecurity = parameter.getMethodAnnotation(MySecurity.class);
                encrypt = mySecurity.decrypt();
            }
            if(encrypt){
                log.info("解密方法{}",parameter.getMethod().getName());
                return new MyHttpInputMessage(inputMessage);
            }else {
                return inputMessage;
            }
        } catch (IOException e) {
            log.error(parameter.getMethod().getName()+"方法解密异常{}",e.getMessage());
            return inputMessage;
        }
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return null;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    class MyHttpInputMessage implements HttpInputMessage{
        private HttpHeaders httpHeaders;

        private InputStream body;

        public MyHttpInputMessage(HttpInputMessage httpInputMessage) throws IOException {
            this.httpHeaders = httpInputMessage.getHeaders();
            this.body = IOUtils.toInputStream(easpString((IOUtils.toString(httpInputMessage.getBody(),"UTF-8"))), "UTF-8");
        }



        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }

        public String easpString(String requestData){
            if(requestData != null && !"".equals(requestData)){
                Map<String,String> map = JSONObject.parseObject(requestData,Map.class);
                String data = map.get("requestData");
                String encrypted = map.get("encrypted");
                if(StringUtils.hasLength(data) || StringUtils.hasLength(encrypted)){
                    throw new RuntimeException("参数【requestData】缺失异常！");
                }else{
                    String content = null;
                    String aseKey = null;
                    try {
                        aseKey = RSAUtils.decryptDataOnJava(encrypted,privateKey);
                    } catch (Exception e) {
                        throw new RuntimeException("参数【aseKey】解析异常！");
                    }
                    try {
                        content = AESUtils.decrypt(data, aseKey);
                    } catch (Exception e) {
                        throw new RuntimeException("参数【content】解析异常！");
                    }
                    if (StringUtils.hasLength(content) || StringUtils.hasLength(aseKey)) {
                        throw new RuntimeException("参数【requestData】解析参数空指针异常!");
                    }
                    return content;
                }
            }
            throw new RuntimeException("参数【requestData】不合法异常！");
        }

    }
}
