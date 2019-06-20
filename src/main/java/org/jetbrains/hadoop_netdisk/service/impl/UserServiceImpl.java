package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.entity.User;
import org.jetbrains.hadoop_netdisk.mapper.UserMapper;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:04
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User query(String username) {
        return userMapper.query(username);
    }
}
