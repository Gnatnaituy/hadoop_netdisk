package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.mapper.UserMapper;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

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

    public int add(User user) {
        return userMapper.add(user);
    }

    public int delete(User user) {
        return userMapper.delete(user);
    }

    public int update(User user) {
        return userMapper.update(user);
    }

    public int updateUsedCapacity(User user) {
        return userMapper.updateUsedCapacity(user);
    }

    public User checkLogin(String username, String password) {
        User user = userMapper.query(username);

        if (user != null && user.getHashedPassword().equals(MD5Util.getStringMD5(password))) {
            return user;
        } else {
            return null;
        }
    }

    public User getCurrentUser(HttpSession session) {
        Object currentUser = session.getAttribute("currentUser");

        return currentUser == null ? null : userMapper.query(currentUser.toString());
    }
}
