package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.entity.HadoopUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:03
 * @description
 */
public interface HadoopUserService {
    HadoopUser query(String username);

    int add(HadoopUser hadoopUser);

    int delete(HadoopUser hadoopUser);

    int update(HadoopUser hadoopUser);

    int updateUsedCapacity(HadoopUser hadoopUser);

    void register(HadoopUser hadoopUser);

    HadoopUser checkLogin(String username, String password);

    HadoopUser getCurrentUser(HttpServletRequest request);
}
