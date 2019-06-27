com_web_video_js=function(){
    var connector = this;

    console.log("-------------------");
    console.log(document.getElementById("my_video_1"))
    console.log(55555);

    var cameraId;
    $(function(){
        cameraId= window.location.href.split("=")[1];
        var src = "m3u8/Gear"+cameraId+"/playlist.m3u8";
        var myPlayer = videojs("my_video_1", {}, function(){

            var obj  = this;
            obj.src({type:"application/x-mpegURL",src:src});
            obj.load();

        });
        //加载完后直接播放
        myPlayer.play();

        $(window).unbind('beforeunload');
        $(window).bind('beforeunload',function(){
            if(myPlayer != null)
                myPlayer.dispose();
            teardown();
            //console.warn("Closing");
            return "Closing the Player";

        });

        function teardown(){
            $.ajax({
                url: "http://10.200.0.11:8080/camWebsite/camera/" + cameraId + "/teardown",
                type: 'GET'
            });
        }

    });

    /*window.addEventListener( "beforeunload", function(event) {
      $.ajax({
        url: "http://172.16.10.152:8080/camWebsite/camera/" + cameraId + "/teardown",
        type: 'GET'
        });
      event.returnValue =  "Closing the Player";
  });*/
}