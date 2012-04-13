Ext.define('Property', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
        fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'dataset_id',
		type : 'int'
	}, {
		name : 'value',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/prop',
			create : 'service/catalog/json/prop/create',
			update : 'service/catalog/json/prop/update',
			destroy : 'service/catalog/json/prop/delete'
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