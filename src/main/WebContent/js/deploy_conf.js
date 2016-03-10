require.config({
	paths : {
		jquery : "jquery-1.11.3.min",
		juicer : "Juicer/src/juicer",
		deploy : "deploy",
		bootstrap : "bootstrap"
	}
});

require([ 'jquery', "bootstrap", 'juicer',  'deploy' ], function() {
});
