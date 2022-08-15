package cn.zjk.npp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @description: controller层日志切面
 * @Author zjk
 * @className: LogAspect
 * @date: 2022/8/10 9:15
 */

@Aspect
@Component
@Slf4j
public class LogAspect {
    private long startTime;
    private long endTime;

    @Pointcut("execution(public * cn.zjk.npp.controller.*.*(..))")
    public void pointCut(){}

    @Around(value = "pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes !=null;

            HttpServletRequest request = attributes.getRequest();
            startTime = System.currentTimeMillis();
            log.info("请求URL：{}",request.getRequestURL().toString());
            log.info("请求方式：{}",request.getMethod());
            log.info("请求ip：{}", getIp(request));
            log.info("请求controller：{}",proceedingJoinPoint.getSignature().getDeclaringType());
            log.info("请求方法：{}",proceedingJoinPoint.getSignature().getName());
            log.info("请求参数：{}", Arrays.toString(proceedingJoinPoint.getArgs()));

            Object result = proceedingJoinPoint.proceed();
            endTime = System.currentTimeMillis();
            log.info("请求耗时：{}",endTime-startTime+"ms");
            log.info("返回结果：{}",result);
            return result;
        } catch (Throwable e) {
            log.error(e.getMessage());
            return new Exception();
        }
    }

    /**
     * ip转换
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }
}
