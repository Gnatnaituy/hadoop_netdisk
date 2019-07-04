package org.jetbrains.hadoop_netdisk.controller;

import com.sun.org.apache.regexp.internal.REUtil;
import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.jetbrains.hadoop_netdisk.service.HadoopFileService;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @auther hasaker
 * @create_date 2019-06-20 10:17
 * @description
 */
@Controller
@RequestMapping("file")
public class HadoopFileController {
    private final HadoopFileService hadoopFileService;
    private final HdfsService hdfsService;

    private final String CURRENT_USER = "currentUser";
    private final String CURRENT_PATH = "currentPath";
    private final String PATHS = "paths";
    private final String USER_HADOOP_FILES = "userHadoopFiles";
    private final String USER_MYSQL_FILES = "userMySQLFiles";
    private final String USER_DELETED_FILES = "userDeletedFiles";
    private final String SHARED_FILES = "sharedFiles";

    private final String MSG = "msg";

    private Logger logger = LoggerFactory.getLogger(HadoopFileController.class);

    public HadoopFileController(HadoopFileService hadoopFileService, HdfsService hdfsService) {
        this.hadoopFileService = hadoopFileService;
        this.hdfsService = hdfsService;
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
     * Update file manager nav bar
     */
    @GetMapping("updateNavbar")
    public String updateNavbar(Model model, HttpServletRequest request) {
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        List<String[]> paths = new ArrayList<>();
        StringBuilder absPath = new StringBuilder();
        for (String path : currentPath.split("/")) {
            absPath.append(path).append("/");
            paths.add(new String[]{path, absPath.toString()});
        }

        model.addAttribute(PATHS, paths);

        return "main::navbar_refresh";
    }

    /**
     * Update file manager
     */
    @GetMapping("updateFileManager")
    public String updateFileManager(Model model, HttpServletRequest request) {
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();
        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);
        
        return "main::file_manager_refresh";
    }

    /**
     * Update shared files
     */
    @GetMapping("updateShare")
    public String updateShare(Model model, HttpServletRequest request) {
        List<HadoopFile> sharedFiles = hadoopFileService.getSharedFiles();

        model.addAttribute(SHARED_FILES, sharedFiles);

        return "main::share_refresh";
    }

    /**
     * Update trash
     */
    @GetMapping("updateTrash")
    public String updateTrash(Model model, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute(CURRENT_USER).toString();
        List<HadoopFile> userDeletedFiles = hadoopFileService.getUserDeletedFiles(currentUser);

        model.addAttribute(USER_DELETED_FILES, userDeletedFiles);

        return "main::trash_refresh";
    }

    /**
     * Search file by given keyword
     */
    @GetMapping("/search")
    public String search(@RequestParam String query, @RequestParam String isSearchShared, Model model,
                         HttpServletRequest request) {
        List<HadoopFile> searchResults;
        String currentUser = request.getSession().getAttribute(CURRENT_USER).toString();

//        if ("true".equals(isSearchShared)) {
//            searchResults = hadoopFileService.searchSharedFiles(query);
//            model.addAttribute(SHARED_FILES, searchResults);
//        } else {
//            searchResults = hadoopFileService.searchFiles(query);
//            model.addAttribute(USER_MYSQL_FILES, searchResults);
//        }
//
//        return "true".equals(isSearchShared) ? "main::share_refresh" : "main::search_refresh";

        if ("true".equals(isSearchShared)) {
            searchResults = hadoopFileService.searchSharedFiles(query);
        } else {
            searchResults = hadoopFileService.searchFiles(currentUser, query);
        }

        model.addAttribute(SHARED_FILES, searchResults);

        return "main::share_refresh";
    }

    /**
     * Upload File from local machine to Hadoop
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request) {
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        hadoopFileService.upload(currentPath, file);

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Download file from Hadoop to Local directly
     */
    @PostMapping("/download")
    public String download(@RequestParam String hashCode) {
        hadoopFileService.download(hashCode);

        return "main::file_manager_refresh";
    }

    /**
     * Make directory
     */
    @PostMapping("/mkdir")
    public String mkdir(@RequestParam String directory, Model model, HttpServletRequest request) {
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        hadoopFileService.mkdir(currentPath + "/" + directory);

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Change dir
     */
    @GetMapping("/chdir")
    public String chdir(@RequestParam(value = "desPath") String desPath, Model model, HttpServletRequest request) {
        request.getSession().setAttribute(CURRENT_PATH, desPath);

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(desPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Rename file or directory
     */
    @PostMapping("/rename")
    public String rename(@RequestParam String oldHdfsPath, @RequestParam String newFileName,
                         @RequestParam boolean isDir, @RequestParam String hashCode,
                         Model model, HttpServletRequest request) {
        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        hadoopFileService.rename(oldHdfsPath, newFileName, isDir, hashCode);

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Share file
     */
    @PostMapping("/share")
    public String share(@RequestParam String shareExpireDay, @RequestParam String shareEncryptCode,
                        @RequestParam String hashCode, Model model) {
        hadoopFileService.share(shareExpireDay, shareEncryptCode, hashCode);
        List<HadoopFile> sharedFiles = hadoopFileService.getSharedFiles();
        model.addAttribute(SHARED_FILES, sharedFiles);

        return "main::share_refresh";
    }

    /**
     * Verify share encrypt code
     */
    @PostMapping("/verify")
    @ResponseBody
    public String verify(@RequestParam String hashCode, @RequestParam String code) {
        return hadoopFileService.verifyShareCode(hashCode, code);
    }

    /**
     * Mark file deleted
     */
    @PostMapping("/markFileDeleted")
    public String markFileDeleted(@RequestParam String hashCode, Model model, HttpServletRequest request) {
        hadoopFileService.fakeDelete(hashCode, request.getSession().getAttribute(CURRENT_USER).toString());

        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Mark directory deleted
     */
    @PostMapping("/markDirDeleted")
    public String markDirDeleted(@RequestParam String hdfsPath, Model model, HttpServletRequest request) {
        hadoopFileService.fakeDeleteDir(hdfsPath, request.getSession().getAttribute(CURRENT_USER).toString());

        String currentPath = request.getSession().getAttribute(CURRENT_PATH).toString();

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);

        return "main::file_manager_refresh";
    }

    /**
     * Restore delete file
     */
    @PostMapping("/restore")
    public String restore(@RequestParam String hashCode, Model model, HttpServletRequest request) {
        // cancel delete from mysql
        hadoopFileService.cancelFakeDelete(hashCode, request.getSession().getAttribute(CURRENT_USER).toString());
        // update deleted file list
        String currentUser = request.getSession().getAttribute(CURRENT_USER).toString();
        List<HadoopFile> userDeletedFiles = hadoopFileService.getUserDeletedFiles(currentUser);

        model.addAttribute(USER_DELETED_FILES, userDeletedFiles);

        return "main::trash_refresh";
    }

    /**
     * Real delete file
     */
    @PostMapping("/realDelete")
    public String realDelete(@RequestParam String hashCode, Model model, HttpServletRequest request) {
        hadoopFileService.realDelete(hashCode);

        List<HadoopFile> userDeletedFiles =
                hadoopFileService.getUserDeletedFiles(request.getSession().getAttribute("currentUser").toString());
        model.addAttribute("userDeletedFiles", userDeletedFiles);

        return "main::trash_refresh";
    }

    /**
     * Real delete all deleted files
     */
    @GetMapping("/emptyTrash")
    public String emptyTrash(Model model, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute(CURRENT_USER).toString();
        for (HadoopFile deletedFile : hadoopFileService.getUserDeletedFiles(currentUser)) {
            hadoopFileService.realDelete(deletedFile.getHashCode());
        }

        model.addAttribute("userDeletedFiles", new ArrayList<>());

        return "main::trash_refresh";
    }
}
