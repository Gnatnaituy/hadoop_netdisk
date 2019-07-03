package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.entity.HadoopUser;
import org.jetbrains.hadoop_netdisk.mapper.HadoopUserMapper;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.HadoopUserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hasaker
 * @create_date 2019-06-19 17:04
 * @description
 */
@Service
public class HadoopUserServiceImpl implements HadoopUserService {

    private final HadoopUserMapper hadoopUserMapper;
    private final HdfsService hdfsService;

    public HadoopUserServiceImpl(HadoopUserMapper hadoopUserMapper, HdfsService hdfsService) {
        this.hadoopUserMapper = hadoopUserMapper;
        this.hdfsService = hdfsService;
    }

    public HadoopUser query(String username) {
        return hadoopUserMapper.query(username);
    }

    public int add(HadoopUser hadoopUser) {
        return hadoopUserMapper.add(hadoopUser);
    }

    public int delete(HadoopUser hadoopUser) {
        return hadoopUserMapper.delete(hadoopUser);
    }

    public int update(HadoopUser hadoopUser) {
        return hadoopUserMapper.update(hadoopUser);
    }

    public int updateUsedCapacity(HadoopUser hadoopUser) {
        return hadoopUserMapper.updateUsedCapacity(hadoopUser);
    }

    public void register(HadoopUser hadoopUser) {
        hadoopUser.setPassword(MD5Util.getStringMD5(hadoopUser.getPassword()));
        hadoopUser.setBio(hadoopUser.getBio());

        // Store hadoopUser information to MySQL
        add(hadoopUser);
        // Create home directory and favorite for hadoopUser on Hadoop
        hdfsService.mkdir(hadoopUser.getUsername());
        hdfsService.mkdir(hadoopUser.getUsername() + "/" + "我的收藏");
    }

    public HadoopUser checkLogin(String username, String password) {
        HadoopUser hadoopUser = hadoopUserMapper.query(username);

        if (hadoopUser != null && hadoopUser.getPassword().equals(MD5Util.getStringMD5(password))) {
            return hadoopUser;
        } else {
            return null;
        }
    }

    public HadoopUser getCurrentUser(HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        return currentUser == null ? null : hadoopUserMapper.query(currentUser);
    }
}
