package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:06
 * @description
 */
@Repository
public interface UserMapper {
    User query(String username);
    int add(User user);
    int delete(User user);
    int update(User user);
    int updateUsedCapacity(User user);
}
