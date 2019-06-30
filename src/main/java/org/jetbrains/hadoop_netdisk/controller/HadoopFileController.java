package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.jetbrains.hadoop_netdisk.entity.HadoopUser;
import org.jetbrains.hadoop_netdisk.service.HadoopFileService;
import org.jetbrains.hadoop_netdisk.service.HadoopUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther hasaker
 * @create_date 2019-06-20 10:17
 * @description
 */
@Controller
@RequestMapping("file")
public class HadoopFileController {
    private final HadoopFileService hadoopFileService;
    private Logger logger = LoggerFactory.getLogger(HadoopFileController.class);

    public HadoopFileController(HadoopFileService hadoopFileService) {
        this.hadoopFileService = hadoopFileService;
    }

    /**
     * Get file detail information
     */
    @GetMapping("query/{hashCode}")
    public String query(@PathVariable String hashCode) {
        HadoopFile hadoopFile = hadoopFileService.query(hashCode);

        return hadoopFile == null ? "hadoopFile not found": hadoopFile.toString();
    }

    /**
     * Upload File from local machine to Hadoop
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        hadoopFileService.upload(currentUser, file);

        return "redirect:/user/main";
    }

    /**
     * Download file from Hadoop to Local directly
     */
    @GetMapping("/download")
    public String download(@RequestParam String filename) {
        hadoopFileService.download(filename);

        return "redirect:/user/main";
    }

    /**
     * Make directory
     */
    @PostMapping("/mkdir")
    public String mkdir(@RequestParam String directory, HttpServletRequest request) {
        hadoopFileService.mkdir(hadoopFileService.getCurrentDir(request) + "/" + directory);

        return "redirect:/user/main";
    }

    /**
     * Rename file or directory
     */
    @PostMapping("/rename")
    public String rename(@RequestParam String oldHdfsPath, @RequestParam String newFileName,
                         @RequestParam boolean isDir, @RequestParam String hashCode) {
        hadoopFileService.rename(oldHdfsPath, newFileName, isDir, hashCode);

        return "redirect:/user/main";
    }

    /**
     * Share file
     */
    @PostMapping("/share")
    public String share(@RequestParam boolean shareEncrypt, @RequestParam String shareEncryptCode,
                        @RequestParam String hashCode) {
        hadoopFileService.share(shareEncrypt, shareEncryptCode, hashCode);

        return "redirect:/user/main";
    }

    /**
     * Mark file deleted
     */
    @PostMapping("/markDeleted")
    public String markDeleted(@RequestParam String hashCode) {
        hadoopFileService.fakeDelete(hashCode);

        return "redirect:/user/main";
    }

    /**
     * Real delete file
     */
    @PostMapping("/delete")
    public String delete(@RequestParam String hashCode) {
        hadoopFileService.realDelete(hashCode);

        return "redirect:/user/main";
    }

}
