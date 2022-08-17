package cn.zjk.npp.config;

import cn.zjk.npp.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: WebConfig
 * @Author zjk
 * @className: MyWebConfig
 * @date: 2022/8/8 15:25
 */

@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    /**
     * 跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                //拦截路径
                .addPathPatterns("/**")
                //登录接口排除
                .excludePathPatterns("/doc.html","/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }
}
