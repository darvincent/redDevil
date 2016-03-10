define([ 'util', 'protocal', 'loadInfo' ], function() {
	var client = '', server = '';
	var deployID = util().getLinkParam(window.location.href, "deployID");
	$.get("deployments", {
		"type" : "1",
		"D1" : deployID
	}, function(returnMsg, status) {
		var content = '';
		if (status === 'success') {
			if (returnMsg != '') {
				var item = eval("(" + returnMsg + ")");
				var errorCode = item["D44"];
				switch (errorCode) {
				case "1":
					$.each(protocal().deployment_properties,function(){
						content += "<tr><td>" + protocal().KV[this] + "</td><td>" + item[this] + "</td></tr>";
					});
					client = item["D2"];
					// server = item["D3"];
					$('title').html(client);
					// var moreInfo = "<button onclick=\"loadPM('productMaster','" +
					// client + "','" + server + "')\" class='btn'>Full Product
					// List</button>";
					// $("#actions").append(moreInfo);
					break;
				case "-1":
					// can't get DB connection
					content = "Query error!";
					break;
				case "-2":
					// request input missed some require data
					content = "Query error!";
					break;
				case "-3":
					// query DB error
					content = "Query error!";
					break;
				default:
					content = "Query error!";
					break;
				}
			}
		} else {
			content = "Query error!";
		}
		$("#properties").append(content);

		$("#export").click(function() {
			$("#buttons").hide();
			window.print();
			$("#buttons").show();
		});
	});
});
