Ext.define('ServiceType', {
	extend: 'Ext.data.Model',
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
			read : 'service/srvctype/json/default',
			create : 'service/srvctype/json/default/create',
			update : 'service/srvctype/json/default/update',
			destroy : 'service/srvctype/json/default/delete'
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