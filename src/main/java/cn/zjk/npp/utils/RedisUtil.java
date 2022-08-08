package cn.zjk.npp.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description: TODO
 * @Author zjk
 * @className: RedisUtil
 * @date: 2022/8/8 15:08
 */
@Service
public class RedisUtil {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key, value);
    }
    public void set(String key,Object value,Long time){
        redisTemplate.opsForValue().set(key,value,time,TimeUnit.SECONDS);
    }

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long delete(Set<String> keys) {
        return redisTemplate.delete(keys);
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
