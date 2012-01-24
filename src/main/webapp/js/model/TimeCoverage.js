Ext.define('TimeCoverage', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
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
			read : 'service/catalog/json/time',
			create : 'service/catalog/json/time/create',
			update : 'service/catalog/json/time/update',
			destroy : 'service/catalog/json/time/delete'
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