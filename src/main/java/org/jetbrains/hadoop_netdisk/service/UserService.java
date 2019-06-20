package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.entity.User;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:03
 * @description
 */
public interface UserService {
    User query(String username);
}
