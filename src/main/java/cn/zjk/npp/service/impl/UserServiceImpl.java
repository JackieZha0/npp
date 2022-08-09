package cn.zjk.npp.service.impl;

import cn.zjk.npp.mapper.UserMapper;
import cn.zjk.npp.model.User;
import cn.zjk.npp.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: TODO
 * @Author zjk
 * @className: UserServiceImpl
 * @date: 2022/8/9 13:24
 */

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public List<User> getUserInfo() {
        return userMapper.queryAll();
    }

    @Override
    public User getUserById(int id) {
        return userMapper.queryUserById(id);
    }
}
