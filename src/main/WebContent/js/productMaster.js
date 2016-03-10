define([ 'util', 'protocal', 'loadInfo' ], function() {
	loadInfo().ClientList("D2", 'D2_err');
	$("#loadServer").click(function() {
		loadInfo().ClientServers($("#D2").val(), "servers", "D3", "D3_err")
	});
	$("#search").click(function() {
		var client = $.trim($("#D2").val());
		var server = $.trim($("#D3").val());
		if (client === '' || server === '') {
			alert("Must select client and server!");
		} else {
			loadInfo().PM('productMaster', client, server);
		}
	});
	$("#D2").change(function() {
		$("#D3").val('');
	})
})