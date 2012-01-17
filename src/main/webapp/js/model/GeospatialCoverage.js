//TODO
Ext.define('GeospatialCoverage', {
	extend: 'Ext.data.Model',
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
		name : 'controlled_vocabulary_id',
		type : 'int'
	}, {
		name : 'zpositive_id',
		type : 'int'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/geospatial/json/default',
			create : 'service/geospatial/json/default/create',
			update : 'service/geospatial/json/default/update',
			destroy : 'service/geospatial/json/default/delete'
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