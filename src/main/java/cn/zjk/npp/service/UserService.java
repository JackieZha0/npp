package cn.zjk.npp.service;

import cn.zjk.npp.model.User;

import java.util.List;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserService
 * @date: 2022/8/9 13:23
 */
public interface UserService {
    /**
     * 获取所有用户信息
     * @return
     */
    List<User> getUserInfo();

    /**
     * 根据id查询
     * @param id
     * @return
     */
    User getUserById(int id);
}
