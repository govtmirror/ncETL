Ext.define('Documentation', {
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
		name : 'documentation_type_id',
		type : 'int'
	}, {
		name : 'xlink_href',
		type : 'string'
	}, {
		name : 'xlink_title',
		type : 'string'
	}, {
		name : 'text',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/doc',
			create : 'service/catalog/json/doc/create',
			update : 'service/catalog/json/doc/update',
			destroy : 'service/catalog/json/doc/delete'
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