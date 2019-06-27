package org.jetbrains.hadoop_netdisk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;


/**
 * @auther hasaker
 * @create_date 2019-06-27 09:15
 * @description
 */
@Configuration
public class TomcatConfig {

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize MaxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private DataSize MaxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize(MaxFileSize);
        /// 总上传数据大小
        factory.setMaxRequestSize(MaxRequestSize);

        return factory.createMultipartConfig();
    }
}
