package cn.zjk.npp.controller;

import cn.zjk.npp.result.Result;
import cn.zjk.npp.result.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserController
 * @date: 2022/8/8 17:27
 */

@Api("用户模块")
@RestController("/npp/user/")
public class UserController {

    @ApiOperation("获取用户信息")
    @PostMapping("getUserInfo")
    public Result getUser(int userId){
        return ResultUtil.success();
    }
}
