package org.jetbrains.hadoop_netdisk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.jetbrains.hadoop_netdisk.mapper")
public class HadoopNetdiskApplication {

    public static void main(String[] args) {
        SpringApplication.run(HadoopNetdiskApplication.class, args);
    }

}
