$().ready(function () {
    var percentage = Math.round(parseInt($("#used").text()) / parseInt($("#total").text()) * 100);
    $("#capacity").attr("aria-valuenow", percentage).attr("style", "width:" + percentage + "%");
});