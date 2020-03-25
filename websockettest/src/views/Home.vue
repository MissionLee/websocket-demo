<template>
    <div class="home">
        <img alt="Vue logo" src="../assets/logo.png">
        <HelloWorld msg="Welcome to Your Vue.js App"/>
    </div>
</template>

<script>
    // @ is an alias to /src
    import HelloWorld from '@/components/HelloWorld.vue'
    import SocketJS from 'sockjs-client'
    import Stomp from 'stompjs'
    import SocketController from '../plugin/WebSocketUtils'

    export default {
        name: 'Home',
        components: {
            HelloWorld
        },
        created() {
            var uns = null;
            SocketController.init("http://localhost:9090/bc/test");
            SocketController.send("/front/one", 1, "1", {})
            SocketController.subscribe("/back/a", function (message) {
                console.log("收到回信 a")
                SocketController.send("/front/two",1,"hhhh",{a:"aaaaa"})
            },{})
            SocketController.subscribe("/back/b", function (message) {
                console.log("收到回信 b")
                SocketController.send("/front/two",1,"hhhh", {a:"aaaaa"})
            },{id:"a"})

            setTimeout(function(){
                console.log("-----------")
                SocketController.unsubscribe("a")
            },2000)
            setTimeout(function(){
                console.log("++++++=")
                SocketController.send("/front/one", 1, "1", {})

            },5000)
            // SocketController.subscribe("/back/a", function (message) {
            //     console.log("收到回信")
            //     SocketController.send("/front/two",1,"hhhh",{a:"aaaaa"})
            // },{id:2})
            // SocketController.send("/front/one", 1, "2", {})
            // SocketController.send("/front/one", 1, "3，", {})
            // SocketController.send("/front/one", 1, "3，", {})
            // SocketController.send("/front/one", 1, "3，", {})
            // SocketController.send("/front/one", 1, "3，", {})
            // SocketController.send("/front/one", 1, "3，", {})

            // // 连接1
            // let socket1 = new SocketJS("http://localhost:9090/bc/test")
            // socket1.onmessage = function(res){
            //     console.log("socket onmessage")
            // }

            // let stompclient = Stomp.over(socket1);
            // // stompclient.debug = null;
            // // let key = Math.random() * 1000;
            // // console.log(key)
            // stompclient.connect({key:"1"},function(){
            //     console.log("执行了第一个connect")
            //     stompclient.subscribe("back/a",function(message){
            //         console.log("收到信息-11111："+message.body)
            //     })
            // })
            // stompclient.connect({key:"1"},function(){
            //     console.log("执行了第2个connect")
            //
            //     stompclient.subscribe("back/a",function(message){
            //         console.log("收到信息-222222："+message.body)
            //     })
            // })
            // stompclient.connect({key:"1"},function(){
            //     console.log("执行了第3个connect")
            //
            //     stompclient.send("/front/one", {}, "连接1 hello one")
            //     stompclient.send("/front/one", {}, "连接1 hello one")
            //
            // })
            // setTimeout(function(){
            //     stompclient.connect({key:1},function(){
            //         console.log("-------- 10秒之后的 connect执行了")
            //     })
            // },10000)
            // stompclient.connect({key: "1"}, function () {
            //     console.log("连接1，连接成功")
            //
            //     // 订阅 /back/a 这个公共消息
            //     stompclient.subscribe("/back/a", function (message) {
            //         console.log("连接1 公共-  a :" + message.body)
            //     })
            //     // 订阅 /self 这个私人信息
            //     stompclient.subscribe("/auth/" + "1" + "/self", function (message) {
            //         console.log("链接1 私有- b :" + message.body)
            //     })
            //
            //     setTimeout(function () {
            //         stompclient.send("/front/one", {}, "连接1 hello one")
            //     }, 1000)
            //     setTimeout(function () {
            //         stompclient.send("/front/two", {key: "1"}, JSON.stringify({a: "连接1 hello two"}))
            //     }, 3000)
            //
            //
            // })
            // // 连接2
            // let key2 = Math.random() * 1000;
            // console.log(key2)
            // let socket2 = new SocketJS("http://localhost:9090/bc/test")
            // let stompclient2 = Stomp.over(socket2);
            // stompclient2.debug = null;
            // stompclient2.connect({key: "2"}, function () {
            //     console.log("连接2 ，连接成功")
            //     // 订阅 /back/a 这个公共消息
            //     stompclient2.subscribe("/back/a", function (message) {
            //         console.log("连接2 - 公共 :" + message.body)
            //     })
            //     // 订阅 /self 这个私人信息
            //     stompclient2.subscribe("/auth/" + 2 + "/self", function (message) {
            //         console.log("链接2 - 私人 :" + message.body)
            //     })
            //
            //     setTimeout(function () {
            //         stompclient2.send("/front/two", {key: "2"}, {a: "连接2 hello two"})
            //     }, 5000)
            //     setTimeout(function () {
            //         stompclient2.send("/front/two", {key: "2"}, {a: "连接2 hello two"})
            //     }, 10000)
            //
            // })
        }


    }
</script>
