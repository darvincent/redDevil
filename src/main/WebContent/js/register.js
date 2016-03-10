(function($) {
	var items = {
			textBoxes : [ {
				JSONKey : "U1",
				UIKey : protocal().KV["U1"],
				ResultKey : "U1_R",
				Type : "1"
			}, {
				JSONKey : "U3",
				UIKey : protocal().KV["U3"],
				ResultKey : "U3_R",
				Type : "2"
			}, {
				JSONKey : "u3",
				UIKey : protocal().KV["u3"],
				ResultKey : "u3_R",
				Type : "3"
			}, {
				JSONKey : "U6",
				UIKey : protocal().KV["U6"],
				ResultKey : "U6_R",
				Type : "1"
			} ]
		};
	
	
	var html = juicer($("#registerData").html(), items);
	$("#registerForm").append(html);

	$("#submit").click(function() {
		result = true;
		$.each(protocal.textBoxes, function(i, item) {
			$("#" + item.ResultKey).html("");
			if ($.trim($("#" + item.JSONKey).val()) == '') {
				$("#" + item.ResultKey).html("Must Input!")
				result = false;
			}
		});
		if ($("#U3").val() !== $("#u3").val()) {
			$("#u3_R").html("Confirm password is not the same!")
			result = false;
		}
		if (result) {
			$.post("doRegister", {
				"U1" : $("#U1").val(),
				"U3" : $("#U3").val(),
				"U6" : $("#U6").val()
			}, function(returnMsg, status) {
				$("#server_feedback").html("");
				var item = eval("(" + returnMsg + ")");
				var result = item['U11'];
				var content = '';
				switch (result) {
				case "1":
					alert('Register done! You can login now!');
					window.location.href = 'home.html';
					break;
				case "-1":
					content = "Register information is not complete!";
					break;
				case "-2":
					content = 'User already registers!';
					break;
				case "-3":
					content = 'Communication error, please submit again!';
					break;
				case "-4":
					content = 'Communication error, please submit again!';
				default:
					content = 'Communication error, please submit again!';
					break;
				}
				$("#server_feedback").append("<span style='color:red'>" + content + "</span>");
			});
		}
		return false;
	});
})(jQuery)