Ext.define('ServiceType', {
	extend: 'Ext.data.Model',
        belongsTo: 'Service',
	fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'type',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/srvctype',
			create : 'service/catalog/json/srvctype/create',
			update : 'service/catalog/json/srvctype/update',
			destroy : 'service/catalog/json/srvctype/delete'
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