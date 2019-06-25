package org.jetbrains.hadoop_netdisk.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.impl.HdfsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther hasaker
 * @create_date 2019-06-24 10:26
 * @description
 */
@Configuration
public class HdfsConfig {
    @Value("${spring.hadoop.config.fs.defaultFS}")
    private String defaultHdfsUri;

    @Bean
    public HdfsService getHdfsService() {
        org.apache.hadoop.conf.Configuration conf = HBaseConfiguration.create();
        conf.set("fs.defaultFS", defaultHdfsUri);
        conf.set("dfs.client.use.datanode.hostname","true");

        return new HdfsServiceImpl(conf, defaultHdfsUri);
    }
}
