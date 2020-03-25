import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

let WebSocketController = function () {
}

WebSocketController.prototype.doConnectCallBacks = function () {
    var _this = this.___this;
    _this._initing = false;
    // 先绑定所有的订阅
    let i = 0;
    while (i < _this._subscribeList.length) {
        _this._stomp.subscribe(_this._subscribeList[i].dest, _this._subscribeList[i].callback, _this._subscribeList[i].headers)
        i++;
    }
    _this._subscribeList = [];
    let j = 0;
    while (j < _this._sendList.length) {
        _this.doSend(_this._sendList[j].dest, _this._sendList[j].status, _this._sendList[j].msg, _this._sendList[j].data)
        j++;
    }
    _this._sendList = [];
}
WebSocketController.prototype.init = function (url, header) {
    this._url = url;
    this._header = header;
    this._subscribeList = [];
    this._sendList = [];
    this._inited = true;
}
WebSocketController.prototype.doInit = function () {

    if (this._inited) {// 已经填写了初始参数
        if (this._sock && this._stomp && (this._sock.readyState == SockJS.CONNECTING || this._sock.readyState == SockJS.OPEN)) { // 已经创建了客户端，根据客户端状态，判断需要做的事情
            // 如果连接都创建好了，并且连接状态为正则连接 或者 打开，那么不需要做什么事情
            this._initing = false;
            return;
        } else {
            // 如果根本没有初始化，或者当前正在关闭，或者已经关闭，都新建连接，并且在连接成功之后，执行这段时间积累起来的回调
            this._sock = new SockJS(this._url);
            this._stomp = Stomp.over(this._sock);
            this._stomp.___this = this;
            this._stomp.debug = null;

            this._stomp.connect({}, this.doConnectCallBacks)
        }
    } else { // 没填写初始参数
        this._initing = false;
        throw new Error("发送/订阅灯操作前，必须使用#init()方法初始化系统")
    }

}


WebSocketController.prototype.send = function (dest, status, msg, data) {
    if (!this._initing){
        this._initing = true;
        this.doInit()
    }
    if (this._sock.readyState == SockJS.OPEN) { // 如果现在是可以发送的状态，那么发送
        this.doSend(dest, status, msg, data)
    } else { // 否则，加入发送列表
        this._sendList[this._sendList.length] = {dest: dest, status: status, msg: msg, data: data}
    }
}
WebSocketController.prototype.doSend = function (dest, status, msg, data) {
    var info = {};
    info.status = status;
    info.msg = msg;
    info.data = data;
    this._stomp.send(dest, {key: "1"}, JSON.stringify(info))
}
WebSocketController.prototype.subscribe = function (dest, callback, headers) {
    headers.id = dest;
    if (!this._initing){
        this._initing = true;
        this.doInit()
    }
    if (this._sock.readyState == SockJS.OPEN) {
        this._stomp.subscribe(dest, callback, headers)
    } else {
        this._subscribeList[this._subscribeList.length] = {dest: dest, callback: callback, headers: headers}
    }
}
// WebSocketController.prototype.unsubscribe = function(id){
//     this._stomp.unsubscribe(id)
// }
let s = new WebSocketController();
export default s;