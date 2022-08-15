package cn.zjk.npp.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserBo
 * @date: 2022/8/9 16:41
 */

@Data
@ApiModel("用户")
public class UserBO {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("手机")
    private String phone;
    @ApiModelProperty("email")
    private String email;
}
