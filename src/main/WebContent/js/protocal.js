(function() {
	var staticData = {};

	staticData.action = {};
	staticData.action.LOGIN = "1";
	staticData.action.SYNC = "2";
	staticData.action.DEPLOY = "3";
	staticData.action.LOAD_CLIENTLIST = "4";
	staticData.action.LOAD_CLIENTSERVER = "5";
	staticData.action.LOAD_CLIENTCONTACT = "6";
	staticData.action.LOAD_CLIENTSERVERPRODUCT = "7";
	staticData.action.LOAD_PRODUCTLIST = "8";
	staticData.action.QUERY_PRODUCTVERSION = "9";

	staticData.status = {};
	staticData.status.NEW = "new";
	staticData.status.RECEIVED = "received";
	staticData.status.REPLIED = "replied";
	staticData.status.CONFIRMED = "confirmed";
	staticData.status.ERROR = "error";

	staticData.key = {};
	staticData.key.MSGID = "msgID";
	staticData.key.STATUS = "status";
	staticData.key.ACTION = "action";
	staticData.key.BODY = "body";
	staticData.key.ERRORCODE = "errorCode";

	staticData.SEPERATOR1 = ",";

	staticData.KV = {
		"U1" : "UserName",
		"U3" : "Password",
		"u3" : "Confirm",
		"U6" : "Email",
		"D1" : "DeployID",
		"D2" : "Client",
		"D3" : "Server",
		"D4" : "contact",
		"D5" : "Priority",
		"D6" : "RequestType",
		"D7" : "Environment",
		"D8" : "Description",
		"D9" : "Component",
		"D10" : "Version",
		"D11" : "Details",
		"D13" : "DeployedBy",
		"D14" : "DeployTime"
	};

	staticData.deployment_properties = new Array("D1", "D2", "D3", "D4", "D5", "D6", "D7", "D11", "D8", "D13", "D14");

	protocal = function() {
		return staticData;
	}

})()
