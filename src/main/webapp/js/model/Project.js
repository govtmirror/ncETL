Ext.define('Project', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
	fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'controlled_vocabulary_id',
		type : 'int'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/project',
			create : 'service/catalog/json/project/create',
			update : 'service/catalog/json/project/update',
			destroy : 'service/catalog/json/project/delete'
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