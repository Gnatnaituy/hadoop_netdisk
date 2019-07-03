// 点击上传按钮直接选择并上传文件
$(document).ready(function () {
    $("#uploadButton").click(function () {
        $("#uploadInput").click();
    });

    $("#uploadInput").change(function () {
        $("#uploadForm").submit();
    })
});

// 新建文件夹 搜索 搜索分享文件
$(document).ready(function () {
    $("#mkdirForm").hide();
    $("#searchForm").hide();

    $("#mkdirButton").click(function () {
        var mkdirInput = $("#mkdirInput");

        if (mkdirInput.is(':hidden')) {
            $("#mkdirForm").show();
        } else {
            if (mkdirInput.val() === "") {
                $("#mkdirForm").hide()
            } else {
                $("#mkdirForm").submit();
            }
        }
    });

    $("#searchButton").click(function () {
        var searchInput = $("#searchInput");

        if (searchInput.is(':hidden')) {
            $("#searchForm").show();
        } else {
            if (searchInput.val() === "") {
                $("#searchForm").hide();
            } else {
                $("#searchForm").submit();
            }
        }
    });
});

// 搜索热门资源的局部更新
$(document).ready(function () {
    var searchSharedForm = $("#searchSharedForm");
    var searchSharedInput = $('#searchSharedInput');
    searchSharedForm.hide();

    $("#searchSharedButton").click(function () {
        if (searchSharedForm.is(':hidden')) {
            searchSharedForm.show();
        } else {
            if (searchSharedInput.val() === "") {
                searchSharedForm.hide();
            } else {
                searchSharedForm.submit();
                // $('#share_refresh').load('/file/query?isSearchShared=true&query=' + $('#searchSharedInput').val());
                $.ajax({
                    url: '/file/search',
                    type: 'get',
                    data: {
                        isSearchShared: 'true',
                        query: searchSharedInput.val()
                    },
                    success: function (data) {
                        $('#shareFileListAccordion').html(data);
                    }
                });
            }
        }
    });
});


// 阻止点击文件夹名称的冒泡事件
$(function () {
    $("a#dirId").click(function (event) {
        event.stopPropagation();
    })
});

// 计算已使用容量的百分比
$().ready(function () {
    var percentage = Math.round(parseInt($("#used").text()) / parseInt($("#total").text()) * 100);
    $("#capacity").attr("aria-valuenow", percentage).attr("style", "width:" + percentage + "%");
});
