package cn.zjk.npp.interceptor;

import cn.zjk.npp.utils.JwtUtil;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: Jwt拦截器
 * @Author zjk
 * @className: JwtInterceptor
 * @date: 2022/8/17 16:34
 */

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        Map<String,Object> map = new HashMap<>();
        if(token !=null){
            try {
                JwtUtil.verify(token);
                return true;
            } catch (Exception e) {
                map.put("msg","token无效");
            }
        }else{
            map.put("msg","token为空");
        }
        map.put("status",false);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(new Gson().toJson(map));
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("-------------postHandle----------");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("-------------afterCompletion----------");
    }
}
