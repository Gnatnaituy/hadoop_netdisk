package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.mapper.UserMapper;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:04
 * @description
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final HdfsService hdfsService;

    public UserServiceImpl(UserMapper userMapper, HdfsService hdfsService) {
        this.userMapper = userMapper;
        this.hdfsService = hdfsService;
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

    public void register(User user) {
        user.setPassword(MD5Util.getStringMD5(user.getPassword()));
        user.setAge(user.getAge());
        user.setGender(user.getGender());
        user.setBio(user.getBio());

        // Store user information to MySQL
        add(user);
        // Create home directory for user on Hadoop
        hdfsService.mkdir(user.getUsername());
    }

    public User checkLogin(String username, String password) {
        User user = userMapper.query(username);

        if (user != null && user.getPassword().equals(MD5Util.getStringMD5(password))) {
            return user;
        } else {
            return null;
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        return currentUser == null ? null : userMapper.query(currentUser);
    }
}
