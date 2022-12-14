package cn.zjk.npp.controller;

import cn.zjk.npp.anotation.MySecurity;
import cn.zjk.npp.bo.UserBO;
import cn.zjk.npp.model.User;
import cn.zjk.npp.result.Result;
import cn.zjk.npp.result.ResultUtil;
import cn.zjk.npp.service.UserService;
import cn.zjk.npp.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserController
 * @date: 2022/8/8 17:27
 */

@Api("用户模块")
@RestController("/npp/user/")
public class UserController {
    @Resource
    UserService userService;

    @ApiOperation("获取用户信息")
    @PostMapping("getUserInfo")
    @MySecurity(decrypt = false)
    public Result getUser(){
        List<User> userList = userService.getUserInfo();
        return ResultUtil.success(userList);
    }

    @ApiOperation("根据id获取用户信息")
    @PostMapping("getUserInfoById")
    public Result getUserById(@RequestBody UserBO userBO){
        User user = userService.getUserById(userBO.getId());
        return ResultUtil.success(user);
    }

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserBO userBO){
        Map<String,String> map = new HashMap<>();
        Map<String,String> payload = new HashMap<>();
        payload.put("userId",String.valueOf(userBO.getId()));
        payload.put("userName", userBO.getUserName());
        String token = JwtUtil.generateToken(payload);

        map.put("status","OK");
        map.put("token",token);

        return ResultUtil.success(map);
    }
}
