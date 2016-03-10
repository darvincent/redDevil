// require jquery, protocal
//alert("util");
(function() {
	var rootURL = "http://192.168.20.143:8080/redDevil";
	var func = {};
	func.getLinkParam = function(sHref, sArgName) {
		var args = sHref.split("?");
		var retval = "";

		if (args[0] == sHref) {
			return retval;
		}
		var str = args[1];
		args = str.split("&");
		for (var i = 0; i < args.length; i++) {
			str = args[i];
			var arg = str.split("=");
			if (arg.length <= 1)
				continue;
			if (arg[0] == sArgName)
				retval = arg[1];
		}
		return retval;
	}

	func.select = function(id, data) {
		$("#" + id).val(data);
	}

	func.resize = function(ifToTop) {
		if (ifToTop === false) {
			$("#subPage", parent.document).css({
				height : $("body").outerHeight(true) + 100
			});
		} else {
			$("#subPage", parent.document).css({
				height : 0
			});
			$("#subPage", parent.document).css({
				height : $("body").outerHeight(true) + 100
			});
		}
	}

	func.genUrlWithRandomParam = function(relativePath) {
		return rootURL + relativePath + "?n=" + Math.random();
	}

	func.goTop = function(id) {
		$("#" + id).append("<div id='toTop'><img src='1.png'></div>")
		$("#toTop").css({
			width : '50px',
			height : '50px',
			bottom : '200px',
			right : '15px',
			position : 'fixed',
			cursor : 'pointer',
			zIndex : '999999',
		});
		$("#toTop").click(function() {
			$("html,body").animate({
				scrollTop : "0px"
			}, "fast")
		});
	}

	func.isFunction = function(name) {
		return typeof (eval(name)) == "function";
	}

	func.HashTable = function() {
		var size = 0;
		var entry = new Object();
		var table = {};

		table.put = function(key, value) {
			if (!table.containsKey(key)) {
				size++;
			}
			entry[key] = value;
		}

		table.get = function(key) {
			return table.containsKey(key) ? entry[key] : null;
		}

		table.remove = function(key) {
			if (table.containsKey(key) && (delete entry[key])) {
				size--;
			}
		}

		table.containsKey = function(key) {
			return (key in entry);
		}

		table.containsValue = function(value) {
			for ( var prop in entry) {
				if (entry[prop] == value) {
					return true;
				}
			}
			return false;
		}

		table.getValues = function() {
			var values = new Array();
			for ( var prop in entry) {
				values.push(entry[prop]);
			}
			return values;
		}

		table.getKeys = function() {
			var keys = new Array();
			for ( var prop in entry) {
				keys.push(prop);
			}
			return keys;
		}

		table.getSize = function() {
			return size;
		}

		table.clear = function() {
			size = 0;
			entry = new Object();
		}

		return table;
	}

	func.HT = function() {
		var size = 0;
		var entry = new Object();

		this.put = function(key, value) {
			if (!this.containsKey(key)) {
				size++;
			}
			entry[key] = value;
		}

		this.get = function(key) {
			return this.containsKey(key) ? entry[key] : null;
		}

		this.remove = function(key) {
			if (this.containsKey(key) && (delete entry[key])) {
				size--;
			}
		}

		this.containsKey = function(key) {
			return (key in entry);
		}

		this.containsValue = function(value) {
			for ( var prop in entry) {
				if (entry[prop] == value) {
					return true;
				}
			}
			return false;
		}

		this.getValues = function() {
			var values = new Array();
			for ( var prop in entry) {
				values.push(entry[prop]);
			}
			return values;
		}

		this.getKeys = function() {
			var keys = new Array();
			for ( var prop in entry) {
				keys.push(prop);
			}
			return keys;
		}

		this.getSize = function() {
			return size;
		}

		this.clear = function() {
			size = 0;
			entry = new Object();
		}

		return this;
	}

	util = function() {
		return func;
	}

})()