define([ "util", "protocal" ], function() {
	try {
		var pageNo = 1;

		var showDetail = function(deployID) {
			window.open('oneDeployment.html?deployID=' + deployID);
		}

		var getContent = function(type, value, ifGoTop) {
			var info = {};
			info["type"] = type;
			if (type === "0") {
				info["D17"] = value;
			} else if (type === "2") {
				info["D1"] = value;
				info["D2"] = value;
				$("title").html(value);
			}
			$.get("deployments", info, function(returnMsg, status) {
				var content = '';
				if (status === 'success') {
					if (returnMsg !== '') {
						var item = eval("(" + returnMsg + ")");
						var errorCode = item["D44"];
						switch (errorCode) {
						case "1":
							var deployments = item["D22"];
							for ( var index in deployments) {
								var deployID = deployments[index].D1;
								var client = deployments[index].D2;
								var deployedBy = deployments[index].D13;
								var deployedTime = deployments[index].D14;
								var description = deployments[index].D8;
								var desc_display = description.length > 100 ? description.substr(0, 100) + "...." : description;
								content += "<div id='" + deployID + "' class='hero-unit_ian' style='margin-left: 25%; margin-right: 25%'><h3>For " + client
										+ "  , deployed at " + deployedTime + "</h3><h5>" + deployID + " , deployed by " + deployedBy + "</h5><br />" + desc_display
										+ "<br /></div>";
							}
							if (content === '') {
								$("#feedback").html('No more');
							} else {
								$("#content").append(content);
								for ( var i in deployments) {
									var deployID = deployments[i].D1;
									$("#" + deployID).click(function() {
										showDetail($(this).attr("id"));
									});
								}
								// $("#content").on('click', "div,#" +
								// $(this).attr("id"), function() {
								// showDetail($(this).attr("id"));
								// });
							}
							break;
						case "-1":
							// can't get DB connection
							content = "Load deployments failed!";
							break;
						case "-2":
							// request input missed some require data
							content = "Load deployments failed!";
							break;
						case "-3":
							// query DB error
							content = "Load deployments failed!";
							break;
						default:
							content = "Load deployments failed!";
							break;
						}
					} else {
						content = "Load deployments failed!";
					}
				} else {
					content = "Load deployments failed!";
				}
				if (ifGoTop) {
					util().resize(true);
				} else {
					util().resize(false);
				}
			});
		}

		var keyword = util().getLinkParam(window.location.href, "keyword");
		if (keyword === '') {
			$("#showmore").append("<ul class='pager'><li id='more'><a>Earlier deployments</a></li></ul>");
			$("#more").click(function() {
				pageNo++;
				getContent("0", pageNo, false);
			});
			getContent("0", pageNo, true);
		} else {
			getContent("2", keyword, true);
			util().goTop("gotop");
		}

	} catch (err) {
		alert(err);
	}
});
