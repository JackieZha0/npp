package cn.zjk.npp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
/**
 * @description: TODO
 * @Author zjk
 * @className: NewPersonalPlatform
 * @date: 2022/8/8 17:26
 */

@SpringBootApplication
@EnableCaching
@MapperScan("cn.zjk.npp.mapper")
public class NewPersonalPlatform {
    public static void main(String[] args) {
        SpringApplication.run(NewPersonalPlatform.class,args);
    }
}
