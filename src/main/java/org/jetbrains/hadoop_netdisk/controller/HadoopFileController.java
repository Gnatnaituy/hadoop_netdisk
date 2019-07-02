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
    private final String CURRENT_PATH = "currentPath";
    private final String CURRENT_USER = "currentUser";
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
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        hadoopFileService.upload(currentPath, file);

        return "redirect:/user/main";
    }

    /**
     * Download file from Hadoop to Local directly
     */
    @PostMapping("/download")
    public String download(@RequestParam String hashCode) {
        hadoopFileService.download(hashCode);

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
     * Change dir
     */
    @GetMapping("/chdir")
    public String chdir(@RequestParam(value = "desPath") String desPath, HttpServletRequest request) {
        request.getSession().setAttribute(CURRENT_PATH, desPath);

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
    public String share(@RequestParam String shareExpireDay, @RequestParam String shareEncryptCode,
                        @RequestParam String hashCode) {
        hadoopFileService.share(shareExpireDay, shareEncryptCode, hashCode);

        return "redirect:/user/main";
    }

    /**
     * Mark file deleted
     */
    @PostMapping("/markDeleted")
    public String markDeleted(@RequestParam String hashCode, HttpServletRequest request) {
        hadoopFileService.fakeDelete(hashCode, request.getSession().getAttribute(CURRENT_USER).toString());

        return "redirect:/user/main";
    }

    /**
     * Mark directory deleted
     */
    @PostMapping("/markDirDeleted")
    public String markDirDeleted(@RequestParam String hdfsPath, HttpServletRequest request) {
        hadoopFileService.fakeDeleteDir(hdfsPath, request.getSession().getAttribute(CURRENT_USER).toString());

        return "redirect:/user/main";
    }

    /**
     * Restore delete file
     */
    @PostMapping("/restore")
    public String restore(@RequestParam String hashCode, HttpServletRequest request) {
        hadoopFileService.cancelFakeDelete(hashCode, request.getSession().getAttribute(CURRENT_USER).toString());

        return "redirect:/user/main";
    }

    /**
     * Real delete file
     */
    @PostMapping("/realDelete")
    public String realDelete(@RequestParam String hashCode) {
        hadoopFileService.realDelete(hashCode);

        return "redirect:/user/main";
    }

    /**
     * Real delete all deleted files
     */
    @PostMapping("/emptyTrash")
    public String emptyTrash(HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute(CURRENT_USER).toString();
        for (HadoopFile deletedFile : hadoopFileService.getUserDeletedFiles(currentUser)) {
            hadoopFileService.realDelete(deletedFile.getHashCode());
        }

        return "redirect:/user/main";
    }

}
