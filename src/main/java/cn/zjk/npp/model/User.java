package cn.zjk.npp.model;

import lombok.Data;

/**
 * @description: TODO
 * @Author zjk
 * @className: User
 * @date: 2022/8/9 13:23
 */

@Data
public class User {
    private int id;
    private String username;
    private String name;
    private String phone;
    private String email;
}
