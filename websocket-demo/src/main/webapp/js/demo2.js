/**
 * Created by v-hel27 on 2018/9/28.
 */
//socket = new WebSocket("ws://localhost:9094/starManager/websocket/张三");
var socket;
if(typeof(WebSocket) == "undefined") {
    console.log("您的浏览器不支持 WebSocket");
}else{
    console.log("您的浏览器支持 WebSocket");
    //实现化WebSocket对象，指定要连接的服务器地址与端口  建立连接
    // socket = new WebSocket("ws://localhost:9094/starManager/websocket/张三")
    socket = new WebSocket("ws://localhost:8080/websocket");
    //打开事件
    socket.onopen = function() {
        alert("demo2 Socket open");
//            socket.send("这是来自客户端的消息" + location.href + new Date());
    };
    //获得消息事件
    socket.onmessage = function(msg) {
        alert(msg.data);
        //发现消息进入    调后台获取
//            getCallingList();
    };
    //关闭事件
    socket.onclose = function() {
        alert("demo2 Socket close");
    };
    //发生了错误事件
    socket.onerror = function() {
        alert("demo2 Socket error");
    }
    $(window).unload(function(){
        socket.close();
    });
    //             $("#btnSend").click(function() {
    //                  socket.send("这是来自客户端的消息" + location.href + new Date());
    //             });
    //             $("#btnClose").click(function() {
    //                  socket.close();
    //                            		});
}