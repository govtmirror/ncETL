Ext.define('Publisher', {
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
	}, {
		name : 'contact_url',
		type :'string'
	}, {
		name : 'contact_email',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/publisher',
			create : 'service/catalog/json/publisher/create',
			update : 'service/catalog/json/publisher/update',
			destroy : 'service/catalog/json/publisher/delete'
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