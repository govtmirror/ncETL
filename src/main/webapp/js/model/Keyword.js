Ext.define('Keyword', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
	fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'value',
		type : 'string'
	}, {
		name : 'controlled_vocabulary_id',
		type : 'int'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/keyword',
			create : 'service/catalog/json/keyword/create',
			update : 'service/catalog/json/keyword/update',
			destroy : 'service/catalog/json/keyword/delete'
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