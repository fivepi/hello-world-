
var stompClient = null;
var myHost = null,myUserToken = null,myCompanyId = null,myClientType = null;

//重连状态&基本重连次数&当前重连次数&最大重连次数&最小重连时间
var doReconnect = true,baseCount = 10,reconnectCount = 0,maxCount = 50,reconnectTime = 10000;

//连接
    myHost = host;
    myUserToken = userToken;
    myCompanyId = companyId;
    myClientType = clientType;
    var socket = new SockJS(host);
    stompClient = Stomp.over(socket);
    stompClient.debug = null;//如果需要开启stomp的调试日志,需要调试注释这行

    var header = {
    		//userToken，会员或游客的格式userToken:时间戳,刚进入网站的游客的格式  随机字符串:时间戳
    		userToken:userToken,
    		companyId:companyId,//厅主ID
    		clientType:clientType//客户端类型
    	};

    	//stomp 链接
    stompClient.connect(header, function () {
        //js调用安卓的jsCallbackMethod方法
        window.stub.jsCallbackMethod("连接成功");
    	reconnectCount = 0;
    	doReconnect = true;
        //订阅在线人数
        stompClient.subscribe('/user/queue/clients', function (response) {
        	var body = JSON.parse(response.body);
        	var data = body.data;
        	data.virtualSize;//虚拟人数
        	data.realSize;//实际人数
        	//js调用安卓的jsCallbackMethod方法
        	window.stub.jsCallbackMethod("虚拟人数" + data.virtualSize  + "实际人数" + data.realSize);
        });

        //发送消息，我上线了
        stompClient.send('/app/online',{},null);

    },function(error){
    window.stub.jsCallbackMethod("error = " + error);
        if (error.command == "ERROR") {
    			//超过最大连接数，不重连
    			window.stub.jsCallbackMethod("超过最大连接数，不重连");
    			if (error.headers.message.indexOf('IP_MAX') != -1) doReconnect = false;
        } else {
    	    if(error.indexOf('Lost connection') != -1){
    		   if(reconnectCount < maxCount){
    			  if(reconnectCount == 0) console.log('连接已断开，重新连接');
        		   reconnect(reconnectCount);
    		   }
    	    }
    	}
    });
}

//主动断开连接
function disconnect(){
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log('连接已断开');
    window.stub.jsCallbackMethod("连接已断开");
}

//重连
function reconnect(count,userToken) {
	if (doReconnect) {
		var timeOut = count > baseCount ?
			reconnectTime * (count - baseCount) : reconnectTime;
		console.log('当前第' + (reconnectCount + 1) + '次重连，等待' + (timeOut / 1000) + '秒')
		window.stub.jsCallbackMethod('当前第' + (reconnectCount + 1) + '次重连，等待' + (timeOut / 1000) + '秒');
		reconnectCount++;
		setTimeout(function(){
			connect(myHost,myUserToken,myCompanyId,myClientType);
			window.stub.jsCallbackMethod("开始重连");
		},timeOut)
	}
}



