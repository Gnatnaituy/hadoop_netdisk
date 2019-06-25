package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.File;
import org.jetbrains.hadoop_netdisk.service.FileService;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther hasaker
 * @create_date 2019-06-20 10:17
 * @description
 */
@RestController
@RequestMapping("file")
public class FileController {
    private final FileService fileService;
    private final HdfsService hdfsService;

    public FileController(FileService fileService, HdfsService hdfsService) {
        this.fileService = fileService;
        this.hdfsService = hdfsService;
    }

    @GetMapping("query/{fileName}")
    public String query(@PathVariable String fileName) {
        File file = fileService.query(fileName);

        return file == null ? "file not found": file.toString();
    }
}
