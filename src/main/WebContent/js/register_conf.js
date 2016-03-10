require.config({
	paths : {
		jquery : "jquery-1.11.3.min",
		juicer : "Juicer/src/juicer",
		protocal : "protocal",
		register : "register"
	}
});

require([ 'jquery', 'util', 'protocal', 'juicer', 'register' ], function() {
});
