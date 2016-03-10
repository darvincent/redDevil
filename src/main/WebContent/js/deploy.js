define([ "util", "protocal", "loadInfo" ], function() {
	var lastSelectedClientIndex = 0;
	var deployItemFromVersions = new util().HashTable();

	var oneDeployment = {
		action : protocal().action.DEPLOY,
		sets : [ {
			name : "set2",
			inner : [ {
				JSONKey : "D2",
				UIKey : protocal().KV["D2"],
				ErrorKey : "D2_err",
				Type : "2"
			}, {
				JSONKey : "D3",
				UIKey : protocal().KV["D3"],
				ul_Key : "D3_ul",
				button_Key : "D3_button",
				ErrorKey : "D3_err",
				Type : "5"
			}, {
				JSONKey : "D4",
				UIKey : protocal().KV["D4"],
				ul_Key : "D4_ul",
				button_Key : "D4_button",
				ErrorKey : "D4_err",
				Type : "5"
			} ]
		}, {
			name : "set3",
			inner : [ {
				JSONKey : "D5",
				UIKey : protocal().KV["D5"],
				Type : "3",
				Options : [ "high", "middle", "low" ]
			}, {
				JSONKey : "D6",
				UIKey : protocal().KV["D6"],
				Type : "3",
				Options : [ "Bug Fix", "Enhancement", "New System", "Others" ]
			}, {
				JSONKey : "D7",
				UIKey : protocal().KV["D7"],
				Type : "3",
				Options : [ "Production", "User Acceptance Test", "Internal Integration Test" ]
			} ]
		}, {
			name : "set4",
			inner : [ {
				JSONKey : "D9",
				UIKey : protocal().KV["D9"],
				button_Key : "D9_button",
				ErrorKey : "D9_err",
				Type : "6"
			} ]
		}, {
			name : "set5",
			inner : [ {
				JSONKey : "D8",
				UIKey : protocal()["D8"],
				Type : "4"
			} ]
		} ]
	};

	var showProductVersion = function(msg) {
		$("#D9_err").html('');
		var errorMsg = '';
		var errorCode = msg["D44"];
		switch (errorCode) {
		case "1":
			var client = msg["D2"];
			var server = msg["D3"];
			var product = msg["D9"];
			var version = msg["D10"];
			if ($("#D2").val() == client && $("#D3").val() == server && $("#D9").val() == product) {
				deployItemFromVersions.put(product, version);
				$("#" + trimInsideSpace(product)).find(".from").html(version);
			}
			break;
		case "-1":
			// can't get DB connection
			errorMsg = "Load product version failed!";
			break;
		case "-2":
			// request input missed some require data
			errorMsg = "Load product version failed!";
			break;
		case "-3":
			// query DB error
			errorMsg = "Load product version failed!";
			break;
		default:
			errorMsg = "Load product version failed!";
			break;
		}
		if (errorMsg !== "") {
			$("#D9_err").append(errorMsg);
		}
	}

	var removeItem = function(product) {
		deployItemFromVersions.remove(product);
		$("#" + trimInsideSpace(product)).remove();
	}
	var trimInsideSpace = function(product) {
		return product.replace(' ', '');
	}
	var getDetails = function() {
		var details = '';
		var products = deployItemFromVersions.getKeys();
		$.each(products, function(i, product) {
			var toVersion = $("#" + trimInsideSpace(product)).find(".to").val().trim();
			if (toVersion !== '') {
				var fromVersion = deployItemFromVersions.get(product);
				if (fromVersion !== toVersion) {
					details += product + " from " + fromVersion + " to " + toVersion + "; "
				}
			}
		});
		return details;
	}
	var getInfo = function() {
		var values = {};
		var result = true;
		$.each(oneDeployment.sets, function(i, set) {
			$.each(set.inner, function(t, item) {
				var value = $("#" + item.JSONKey).val();
				if (item.JSONKey === "D9") {
					value = getDetails();
					if (value === '') {
						$("#" + item.UIKey).css('color', 'red');
						result = false;
					} else {
						$("#" + item.UIKey).css('color', 'black');
					}
					values["D11"] = value;
				} else {
					if (value === '') {
						$("#" + item.UIKey).css('color', 'red');
						result = false;
					} else {
						$("#" + item.UIKey).css('color', 'black');
					}
					values[item.JSONKey] = value;
				}
			});
		});
		if (result) {
			return values;
		} else {
			return null;
		}
	}

	deploy_result = function(errorCode) {
		var result = false;
		switch (errorCode) {
		case "1":
			alert('Deploy done!');
			result = true;
			break;
		case "-1":
			alert("Deploy failed,please try again!");
			break;
		case "-2":
			alert("Deploy failed,please try again!");
			break;
		case "-3":
			alert("Deploy failed,please try again!");
			break;
		default:
			result = true;
			break;
		}
		return result;
	}

	var initial = function() {
		var html = juicer($("#boxes").html(), oneDeployment);
		$("#properties").append(html);
		util().resize(true);

		loadInfo().ClientList("D2", "D2_err");
		$("#D2").click(function() {
			var selectedIndex = $("#D2").get(0).selectedIndex;
			if (selectedIndex !== lastSelectedClientIndex) {
				$("#D3").val('');
				$("#D3_ul").empty();
				$("#D4").val('');
				$("#D4_ul").empty();
				lastSelectedClientIndex = selectedIndex;
			}
		})

		loadInfo().ProductList("D9", "D9_err");
		$("#D3_button").click(function() {
			loadInfo().ClientServers($("#D2").val(), "D3_ul", "D3", "D3_err");
		});
		$("#D4_button").click(function() {
			loadInfo().ClientContacts($("#D2").val(), "D4_ul", "D4", "D4_err");
		});
		$("#D9_button").click(
				function() {
					var server = $("#D3").val();
					var product = $("#D9").val();
					if (server == "") {
						alert("Please select a client server!");
					} else if (deployItemFromVersions.containsKey(product)) {
						alert("Already add this component!");
					} else {
						var client = $("#D2").val();
						var productId = trimInsideSpace(product);

						$("#items").append(
								"<div class='form-inline' id='" + productId + "'>" + product
										+ ": <a class='input-small from'></a> to <input class='input-small to' type='text'> <button id='cancel_" + productId
										+ "' class='btn btn-warning'>cancel</button><br/><br/></div>");
						$("#cancel_" + productId).click(function() {
							removeItem(product);
						});
						util().resize(false);
						var info = {
							"D2" : client,
							"D3" : server,
							"D9" : product
						};
						deployItemFromVersions.put(product, "");
						info["type"] = "4";
						$.get("productMaster", info, function(returnMsg, status) {
							if (status === 'success') {
								var content = "";
								if (returnMsg != '') {
									var item = eval("(" + returnMsg + ")");
									if (item != '') {
										showProductVersion(item);
									}
								}
							} else {
								alert("Load product version failed!");
							}
						});
					}
				});
		$("#deploy").click(function() {
			if (!parent.isLogin()) {
				alert("please sign in first!");
			} else {
				var info = getInfo();
				if (info === null) {
					alert("Red font items are incorrect !");
				} else {
					parent.wsSend(parent.genMsgID(), oneDeployment.action, protocal().status.NEW, info);
				}
			}
		});
	}

	initial();
})