// 点击上传按钮直接选择并上传文件
$(document).ready(function () {
    $("#uploadButton").click(function () {
        $("#uploadInput").click();
    });

    $("#uploadInput").change(function () {
        $("#uploadForm").submit();
    })
});

// 新建文件夹 和 搜索
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
    })
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
