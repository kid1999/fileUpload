$(document).ready(function(){
        $(document).mousemove(function(e){
            TweenLite.to($('body'),
                .5,
                { css:
                        {
                            backgroundPosition: ""+ parseInt(event.pageX/8) + "px "+parseInt(event.pageY/'12')+"px, "+parseInt(event.pageX/'15')+"px "+parseInt(event.pageY/'15')+"px, "+parseInt(event.pageX/'30')+"px "+parseInt(event.pageY/'30')+"px"
                        }
                });
        });


    // ajax
    $("#name").blur(function () {
        var data = {
            "id":1,
            "name":$("#name").val().toString(),
            "password":$("#password").val().toString()
        };
        $.ajax({
            url:"/registerAjax",
            contentType:"application/json;charset=UTF-8",
            data:JSON.stringify(data),
            dataType:"json",
            type:"post",
            success:function (data) {
                $("#info").text(data['info']);
                $("#name").addClass("")
            }
        });
    });

});


