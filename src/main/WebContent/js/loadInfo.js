// require jquery, bootstrap, util
(function() {
	var func = {};
	func.ClientList = function(displayID, errorDisplayID) {
		$.get("productMaster", {
			"type" : "0"
		}, function(returnMsg, status) {
			$("#" + errorDisplayID).html('');
			var errorMsg = "";
			if (status === 'success') {
				var content = "";
				if (returnMsg != '') {
					var item = eval("(" + returnMsg + ")");
					var errorCode = item["D44"];
					switch (errorCode) {
					case "1":
						var clientList = item["D18"];
						if (clientList != '') {
							var clients = clientList.split(protocal().SEPERATOR1);
							for (index in clients) {
								content += "<option>" + clients[index] + "</option>";
							}
						}
						break;
					case "-1":
						// can't get DB connection
						errorMsg = "<br />Load clientList failed!";
						break;
					case "-2":
						// query DB error
						errorMsg = "<br />Load clientList failed!";
						break;
					default:
						errorMsg = "<br />Load clientList failed!";
						break;
					}
				} else {
					errorMsg = "Load contacts failed!";
				}
				$("#" + displayID).empty();
				$("#" + displayID).append(content);
				util().resize(false);
			} else {
				errorMsg = "<br />Load clientList failed!";
			}
			if (errorMsg !== "") {
				$("#" + errorDisplayID).append(errorMsg);
			}
		});
	}

	func.ClientServers = function(client, displayID, inputID, errorDisplayID) {
		$.get("productMaster", {
			"type" : "1",
			"D2" : client
		}, function(returnMsg, status) {
			$("#" + errorDisplayID).html('');
			var errorMsg = '';
			if (status === 'success') {
				var content = '';
				if (returnMsg != '') {
					var item = eval("(" + returnMsg + ")");
					var errorCode = item["D44"];
					switch (errorCode) {
					case "1":
						var serverList = item["D19"];
						var servers = serverList.split(protocal().SEPERATOR1);
						for ( var index in servers) {
							content += "<li><a>" + servers[index] + "</a></li>";
						}
						$("#" + displayID).empty();
						if (content) {
							$("#" + displayID).append(content);
							$("#" + displayID).on('click', 'li', function() {
								util().select(inputID, servers[$(this).index()]);
							})
						}
						$('#' + displayID + ',.dropdown-toggle').dropdown();
						util().resize(false);
						break;
					case "-1":
						// can't get DB connection
						errorMsg = "Load servers failed!";
						break;
					case "-2":
						// request input missed some require data
						errorMsg = "Load servers failed!";
						break;
					case "-3":
						// query DB error
						errorMsg = "Load servers failed!";
						break;
					default:
						errorMsg = "Load servers failed!";
						break;
					}
				} else {
					errorMsg = "Load contacts failed!";
				}
			} else {
				errorMsg = "Load servers failed!";
			}
			if (errorMsg !== "") {
				$("#" + errorDisplayID).append(errorMsg);
			}
		});
	}

	func.ClientContacts = function(client, displayID, inputID, errorDisplayID) {
		$.get("productMaster", {
			"type" : "2",
			"D2" : client
		}, function(returnMsg, status) {
			$("#" + errorDisplayID).html('');
			var errorMsg = '';
			if (status === 'success') {
				var content = '';
				if (returnMsg != '') {
					var item = eval("(" + returnMsg + ")");
					var errorCode = item["D44"];
					switch (errorCode) {
					case "1":
						var contactList = item["D20"];
						if (contactList != '') {
							var contacts = contactList.split(protocal().SEPERATOR1);
							for (index in contacts) {
								content += "<li><a>" + contacts[index] + "</a></li>";
							}
							$("#" + displayID).empty();
							if (content !== '') {
								$("#" + displayID).append(content);
								$("#" + displayID).on('click', 'li', function() {
									util().select(inputID, contacts[$(this).index()]);
								});
							}
							$('#' + displayID + ',.dropdown-toggle').dropdown();
							util().resize(false);
						}
						break;
					case "-1":
						// can't get DB connection
						errorMsg = "Load contacts failed!";
						break;
					case "-2":
						// request input missed some require data
						errorMsg = "Load contacts failed!";
						break;
					case "-3":
						// query DB error
						errorMsg = "Load contacts failed!";
						break;
					default:
						errorMsg = "Load contacts failed!";
						break;
					}

				} else {
					errorMsg = "Load contacts failed!";
				}
			} else {
				errorMsg = "Load contacts failed!";
			}
			if (errorMsg !== "") {
				$("#" + errorDisplayID).append(errorMsg);
			}
		});
	}

	func.ProductList = function(displayID, errorDisplayID) {
		$.get("productMaster", {
			"type" : "3"
		}, function(returnMsg, status) {
			$("#" + errorDisplayID).html('');
			var errorMsg = '';
			if (status === 'success') {
				var content = "";
				if (returnMsg != '') {
					var item = eval("(" + returnMsg + ")");
					var errorCode = item["D44"];
					switch (errorCode) {
					case "1":
						var produtList = item["D21"];
						if (produtList != '') {
							var products = produtList.split(protocal().SEPERATOR1);
							for (index in products) {
								content += "<option>" + products[index] + "</option>";
							}
						}
						break;
					case "-1":
						// can't get DB connection
						errorMsg = "Load products failed!";
						break;
					case "-2":
						// request input missed some require data
						errorMsg = "Load products failed!";
						break;
					case "-3":
						// query DB error
						errorMsg = "Load products failed!";
						break;
					default:
						errorMsg = "Load products failed!";
						break;
					}

				} else {
					errorMsg = "Load products failed!";
				}
				$("#" + displayID).empty();
				$("#" + displayID).append(content);
				util().resize(false);
			} else {
				errorMsg = "Load products failed!";
			}
			if (errorMsg !== "") {
				$("#" + errorDisplayID).append(errorMsg);
			}
		});
	}

	func.PM = function(displayID, client, server) {
		$("#productMaster").empty();
		$.get("productMaster", {
			"type" : "5",
			"D2" : client,
			"D3" : server
		}, function(returnMsg, status) {
			var content = "";
			if (status === 'success') {
				if (returnMsg != '') {
					var item = eval("(" + returnMsg + ")");
					var errorCode = item["D44"];
					switch (errorCode) {
					case "1":
						var details = item["D23"];
						for (index in details) {
							content += "<tr><td>" + details[index].D9 + "</td><td>" + details[index].D10 + "</td></tr>";
						}
						if (content === '') {
							content = 'No products deployed yet';
						}
						break;
					case "-1":
						// can't get DB connection
						content = "Query error";
						break;
					case "-2":
						// request input missed some require data
						content = "Query error";
						break;
					case "-3":
						// query DB error
						content = "Query error";
						break;
					default:
						content = "Query error";
						break;
					}
				} else {
					content = "Query error";
				}
			} else {
				content = "Query error";
			}

			$("#" + displayID).empty();
			$("#" + displayID).append(content);
			util().resize(false);
		})
	}

	loadInfo = function() {
		return func;
	}

})()
