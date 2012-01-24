Ext.define('Catalog', {
	extend: 'Ext.data.Model',
	idProperty: 'id',
	associations: [{
		type: 'hasMany',
		model: 'Ingestor',
		primaryKey: 'id',
		foreignKey: 'catalog_id',
		autoLoad: true
	}, {
		type: 'hasMany',
		model: 'Dataset',
		primaryKey: 'id',
		foreignKey: 'catalog_id',
		autoLoad: true
	}, {
		type: 'hasMany',
		model: 'Service',
		primaryKey: 'id',
		foreignKey: 'catalog_id',
		autoLoad: true
	}
	],
	fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'name',
		type : 'string'
	}, {
		name : 'location',
		type : 'string'
	}, {
		name : 'expires',
		type : 'date',
		dateFormat : 'Y-m-d'
	}, {
		name : 'version',
		type : 'string'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/catalog',
			create : 'service/catalog/json/catalog/create',
			update : 'service/catalog/json/catalog/update',
			destroy : 'service/catalog/json/catalog/delete'
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