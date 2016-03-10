define([ 'util', 'protocal' ], function() {
	var ws = null;
	var username = '';
	var ifManualClose = false;
	var wsEndPoint = "ws://192.168.20.143:8080/redDevil/echoendpoint";

	isLogin = function() {
		return ws != null;
	}

	wsSend = function(msgID, action, status, content) {
		if (ws) {
			var msg = {};
			msg[protocal().key.MSGID] = msgID;
			msg[protocal().key.ACTION] = action;
			msg[protocal().key.STATUS] = status;
			msg[protocal().key.BODY] = content;
			ws.send(JSON.stringify(msg));
		} else {
			alert("Disconnected to server, please login!");
		}
	}

	genMsgID = function() {
		return username + new Date().getTime();
	}

	var login_result = function(errorCode) {
		switch (errorCode) {
		case "1":
			$("#loginBox").replaceWith($('#logout_box_tpl').html());
			$("#username").html(username);
			$("#logout").click(logout);
			ifManualClose = false;
			break;
		case "-1":
			alert("Invalid username or password!");
			break;
		case "-2":
			// Query exception
			alert("Query error! Code=" + errorCode);
			break;
		case "-3":
			// Can't get DB Connection
			alert("Query error! Code=" + errorCode);
			break;
		case "-4":
			// some request input field didn't exist
			alert("Query error! Code=" + errorCode);
			break;
		default:
			alert("Query error!");
			break;
		}
	}

	var createWS = function(username, pwd) {
		var sock = new WebSocket(wsEndPoint);
		sock.onmessage = function(returnMsg) {
			try {
				var msg = eval("(" + returnMsg.data + ")");
				var msgID = msg[protocal().key.MSGID];
				var status = msg[protocal().key.STATUS];
				var action = msg[protocal().key.ACTION]
				var errorCode = msg[protocal().key.ERRORCODE];
				switch (status) {
				case protocal().status.RECEIVED:
					switch (action) {
					default:
						break;
					}
					break;
				case protocal().status.REPLIED:
					var result;
					switch (action) {
					case protocal().action.LOGIN:
						result = util().isFunction(login_result) && login_result(errorCode);
						break;
					case protocal().action.DEPLOY:
						var deploy_func = $("#subPage")[0].contentWindow.deploy_result;
						result = util().isFunction(deploy_func) && deploy_func(errorCode)
								&& $("#subPage").attr("src", util().genUrlWithRandomParam("/deployments.html"));
						break;
					default:
						break;
					}
					break;
				default:
					break;
				}
			} catch (err) {
				console.log(err);
			}
			if (status === protocal().status.REPLIED) {
				wsSend(msgID, action, protocal().status.CONFIRMED, '');
			}
		};

		sock.onclose = function(evt) {
			if (!ifManualClose) {
				alert("Disconnected to server!");
			}
			ws = null;
			username = '';
			$("#logoutBox").replaceWith($("#login_box_tpl").html());
			$("#verify_login").click(verify_login);
		};

		sock.onopen = function(evt) {
			// login
			var info = {
				'U1' : username,
				'U2' : pwd
			};
			wsSend(genMsgID(), protocal().action.LOGIN, protocal().status.NEW, info);
		};

		sock.onerror = function(evt) {
			alert("error");
		};
		return sock;
	}

	var verify_login = function() {
		username = $.trim($("#name").val());
		pwd = $.trim($("#pwd").val());
		if (username && pwd) {
			ws = ws || createWS(username, pwd);
		} else {
			alert("please input username and password");
		}
	}

	var logout = function() {
		ifManualClose = true;
		ws.close();
	}

	var changeChildIframe = function(iframeSrc, showGoTop) {
		$("#subPage").attr("src", util().genUrlWithRandomParam(iframeSrc));
		$("#gotop").html("");
		if (!!showGoTop) {
			util().goTop("gotop");
		}
	}

	var initial = function() {
		changeChildIframe("/deployments.html", true);
		$("#inout").append($("#login_box_tpl").html());
		$("#verify_login").click(verify_login);
	}

	initial();
	$("#home").click(function() {
		changeChildIframe("/deployments.html", true);
	});
	$("#deploy").click(function() {
		changeChildIframe("/deploy.html", true);
	});
	$("#PM").click(function() {
		changeChildIframe("/productMaster.html", false);
	});
	$("#readMe").click(function() {
		alert("Please use Chrome or IE10 above to browse this website!")
	});
	$("#search").click(function() {
		var keyword = $("#keyword").val().trim();
		var action = keyword && window.open("deployments.html?keyword=" + keyword);
	});
	$('.dropdown-toggle').dropdown();

});
