package org.jetbrains.hadoop_netdisk.service;

import org.apache.hadoop.fs.BlockLocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @auther hasaker
 * @create_date 2019-06-24 15:12
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class HdfsTest {

    @Autowired
    private HdfsService hdfsService;

    @Test
    public void testMkdir(){
        boolean result1 = hdfsService.mkdir("/testDir");
        System.out.println("创建结果：" + result1);

        boolean result2 = hdfsService.mkdir("/testDir/subDir");
        System.out.println("创建结果：" + result2);
    }

    @Test
    public void testListFiles() {
        List<Map<String, Object>> res = hdfsService.listFiles("/", null);

        res.forEach(fileMap -> {
            fileMap.forEach((key, value) -> System.out.println(key + "--" + value));
            System.out.println();
        });
    }

    @Test
    public void testUpload() {
        hdfsService.upload(new File("/Users/hasaker/Documents/K20Pro/magisk.zip"), "/testDir/magisk.zip");
    }

    @Test
    public void testDownload() {
        hdfsService.download("Eminem/WePE_64_V2.0.exe", "/Users/hasaker/Desktop/WePE_64_V2.0.exe");
    }

    @Test
    public void testRename() {
        hdfsService.rename("/Eminem.txt", "/EminemShow.txt");
    }

    @Test
    public void testGetFileBlockLocations() throws IOException {
        BlockLocation[] locations = hdfsService.getFileBlockLocations("/Eminem.txt");

        if(locations != null && locations.length > 0){
            for(BlockLocation location : locations){
                System.out.println(location.getHosts()[0]);
            }
        }
    }
}
