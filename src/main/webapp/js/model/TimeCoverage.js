//TODO
Ext.define('TimeCoverage', {
	extend: 'Ext.data.Model',
	fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'dataset_id',
		type : 'int'
	}, {
		name : 'start_id',
		type : 'int'
	}, {
		name : 'end_id',
		type : 'int'
	}, {
		name : 'duration',
		type : 'string'
	}, {
		name : 'resolution',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/time/json/default',
			create : 'service/time/json/default/create',
			update : 'service/time/json/default/update',
			destroy : 'service/time/json/default/delete'
		},
		reader : {
			type : 'spec',
			idProperty : 'id'
		},
		writer : {
			type : 'kvp',
			writeAllFields : false
		},
		listeners : {
			"exception" : function(proxy, response, operation, options) {
//				console.log("Proxy Exception");
			}
		}
	}
});