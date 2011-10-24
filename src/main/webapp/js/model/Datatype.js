//TODO
Ext.define('Datatype', {
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
			read : 'service/datatype/json/default',
			create : 'service/datatype/json/default/create',
			update : 'service/datatype/json/default/update',
			destroy : 'service/datatype/json/default/delete'
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