$(document).ready(function () {
    hideForms();
    updateCapacity();

    // Stop bubbling event of click directory
    $(function () {
        $("a#dirId").click(function (event) {
            event.stopPropagation();
        });
    });
});

// Upload file
function uploadFile() {
    var uploadInput = $('#uploadInput');
    uploadInput.click();
    uploadInput.change(function () {
        var uploadForm = new FormData();
        uploadForm.append("file", $("#uploadInput").prop('files')[0]);
        $.ajax({
            url: '/file/upload',
            type: 'post',
            data: uploadForm,
            async: true,
            cache: false,
            processData: false,
            contentType: false,
            success: function (data) {
                toastr.success("上传成功!");
                $('#hadoopFileListAccordion').html(data);
                updateCapacity();
                hideForms();
            },
            error: function () {
                alert("Upload Error!");
            }
        });
    })
}

// Create Directory
function mkdir() {
    var mkdirInput = $("#mkdirInput");

    if (mkdirInput.is(':hidden')) {
        $("#mkdirForm").show();
    } else {
        if (mkdirInput.val() === "") {
            mkdirInput.hide()
        } else {
            var mkdirForm = new FormData();
            mkdirForm.append("directory", mkdirInput.val());
            $.ajax({
                url: "/file/mkdir",
                type: "post",
                data: mkdirForm,
                async: true,
                cache: false,
                processData: false,
                contentType: false,
                success: function (data) {
                    toastr.success("创建文件夹成功!");
                    $('#hadoopFileListAccordion').html(data);
                    hideForms();
                }
            })
        }
    }
}

// Search user's file
function search() {
    var searchForm = $('#searchForm');
    var searchInput = $('#searchInput');
    
    if (searchForm.is(':hidden')) {
        searchForm.show();
    } else {
        if (searchInput.val() === "") {
            searchForm.hide();
        } else {
            $.ajax({
                url: "/file/search",
                type: "get",
                data: {
                    isSearchShared: "false",
                    query: searchInput.val()
                },
                async: true,
                success: function (data) {
                    $('#shareFileListAccordion').html(data);
                    console.log(searchInput.val());
                    hideForms();
                }
            });
        }
    }
}

// Search shared files
function searchShared() {
    var searchSharedForm = $('#searchSharedForm');
    var searchSharedInput = $('#searchSharedInput');

    if (searchSharedForm.is(':hidden')) {
        searchSharedForm.show();
    } else {
        if (searchSharedInput.val() === "") {
            searchSharedForm.hide();
        } else {
            $.ajax({
                url: '/file/search',
                type: 'get',
                data: {
                    isSearchShared: "true",
                    query: searchSharedInput.val()
                },
                async: true,
                success: function (data) {
                    $('#shareFileListAccordion').html(data);
                    hideForms();
                }
            });
        }
    }
}

// Change directory
function chdir(directory) {
    $.ajax({
        url: "/file/chdir?desPath=" + directory,
        type: 'get',
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#hadoopFileListAccordion').html(data);
            updateNavbar();
            hideForms();
        },
        error: function () {
            alert("Change directory Error!");
        }
    });
}

// Share a user's file
function share(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);
    form.append("shareExpireDay", $("#" + hashCode + "expireDay").val());
    form.append("shareEncryptCode", $("#" + hashCode + "encryptCode").val());
    $.ajax({
        url: "/file/share",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("分享成功!");
            $('#shareFileListAccordion').html(data);
            hideForms();
        }
    });
}

// Verify share encrypt code
function verify(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);
    form.append("code", $("#code" + hashCode).val());

    $.ajax({
        url: "/file/verify",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            if (data === "true") {
                toastr.success("密码正确, 开始下载!");
                download(hashCode);
            } else {
                toastr.error("密码错误!");
            }
            hideForms();
        }
    });
}

// Rename a file or directory
function rename(hashCode) {
    var form = new FormData();
    form.append("oldHdfsPath", $("#oldHdfsPath" + hashCode).val());
    form.append("hashCode", $("#hashCode" + hashCode).val());
    form.append("isDir", $("#isDir" + hashCode).val());
    form.append("newFileName", $("#newFileName" + hashCode).val());

    $.ajax({
        url: "/file/rename",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("重命名成功!");
            $('#hadoopFileListAccordion').html(data);
            updateShare();
            hideForms();
        }
    });
}

// Download a file
function download(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);

    $.ajax({
        url: "/file/download",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("下载成功!");
            updateFileManager();
        }
    });
}

// Delete a file
function deleteFile(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);

    $.ajax({
        url: "/file/markFileDeleted",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("删除成功!");
            $("#hadoopFileListAccordion").html(data);
            updateTrash();
            updateCapacity();
            updateShare();
            hideForms();
        },
        error: function () {
            alert(hashCode);
        }
    });
}

// Delete a directory
function deleteDir(hdfsPath) {
    var form = new FormData();
    form.append("hdfsPath", hdfsPath);

    $.ajax({
        url: "/file/markDirDeleted",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("删除成功!");
            $('#hadoopFileListAccordion').html(data);
            updateTrash();
            updateCapacity();
            updateShare();
            hideForms();
        }
    });
}

// Restore a file from trash
function restore(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);

    $.ajax({
        url: "/file/restore",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("恢复成功!");
            $('#deletedFileListAccordion').html(data);
            updateFileManager();
            updateShare();
            updateCapacity();
        }
    })
}

// Real delete a file in trash
function realDelete(hashCode) {
    var form = new FormData();
    form.append("hashCode", hashCode);

    $.ajax({
        url: "/file/realDelete",
        type: "post",
        data: form,
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("删除成功!");
            $('#deletedFileListAccordion').html(data);
        }
    })
}

// Empty trash
function emptyTrash() {
    $.ajax({
        url: '/file/emptyTrash',
        type: 'get',
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            toastr.success("清空回收站成功!");
            $('#deletedFileListAccordion').html(data);
        }
    });
}

// Update Capacity
function updateCapacity() {
    $.ajax({
        url: "/user/updateCapacity",
        type: "get",
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $("#capacity").html(data);
            var percentage = Math.round(parseInt($("#capacity-used").text()) / parseInt($("#capacity-total").text()) * 100);
            $("#capacity-progress-bar").attr("aria-valuenow", percentage).attr("style", "width:" + percentage + "%");
        }
    });
}

// Update Navbar
function updateNavbar() {
    $.ajax({
        url: "/file/updateNavbar",
        type: "get",
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#navbar').html(data);
        }
    })
}

// Update FileManager
function updateFileManager() {
    $.ajax({
        url: "/file/updateFileManager",
        type: "get",
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#hadoopFileListAccordion').html(data);
            hideForms();
        }
    })
}

// Update Share
function updateShare() {
    $.ajax({
        url: "/file/updateShare",
        type: "get",
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#shareFileListAccordion').html(data);
            hideForms();
        }
    })
}

// Update Trash
function updateTrash() {
    $.ajax({
        url: "/file/updateTrash",
        type: "get",
        data: {},
        async: true,
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            $('#deletedFileListAccordion').html(data);
        }
    })
}

// Hide all the forms that should be hidden
function hideForms() {
    $("#mkdirForm").hide();
    $("#searchForm").hide();
    $("#searchSharedForm").hide();
    $("#downloadSuccess").alert('close');
    $("#uploadSuccess").alert('close');
}

