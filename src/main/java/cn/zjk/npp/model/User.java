package cn.zjk.npp.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: TODO
 * @Author zjk
 * @className: User
 * @date: 2022/8/9 13:23
 */

@Data
@ApiModel("用户")
public class User {
    @ApiModelProperty("id")
    private int id;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("手机")
    private String phone;
    @ApiModelProperty("email")
    private String email;
}
