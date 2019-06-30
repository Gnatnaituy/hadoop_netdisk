package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.entity.HadoopUser;
import org.springframework.stereotype.Repository;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:06
 * @description
 */
@Repository
public interface HadoopUserMapper {
    HadoopUser query(String username);

    int add(HadoopUser hadoopUser);

    int delete(HadoopUser hadoopUser);

    int update(HadoopUser hadoopUser);

    int updateUsedCapacity(HadoopUser hadoopUser);
}
