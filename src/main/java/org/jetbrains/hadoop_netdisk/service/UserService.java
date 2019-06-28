package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.model.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:03
 * @description
 */
public interface UserService {
    User query(String username);

    int add(User user);

    int delete(User user);

    int update(User user);

    int updateUsedCapacity(User user);

    void register(User user);

    User checkLogin(String username, String password);

    User getCurrentUser(HttpServletRequest request);
}
