package cn.zjk.npp.mapper;

import cn.zjk.npp.model.User;

import java.util.List;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserMapper
 * @date: 2022/8/9 13:25
 */
public interface UserMapper {
    /**
     * 查询全部用户
     * @return
     */
    List<User> queryAll();

    /**
     * 根据id查询
     * @param id
     * @return
     */
    User queryUserById(int id);
}
