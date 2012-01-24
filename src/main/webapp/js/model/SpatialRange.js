Ext.define('SpatialRange', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
        fields: [{
		name : 'id',
		type : 'int'
	}, {
		name : 'start',
		type : 'string'
	}, {
		name : 'size',
		type : 'string'
	}, {
		name : 'resolution',
		type : 'string'
	}, {
		name : 'units',
		type : 'string'
	}, {
		name : 'geospatial_coverage_id',
		type : 'int'
	}, {
		name : 'spatial_range_type_id',
		type : 'int'
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/spatialrange',
			create : 'service/catalog/json/spatialrange/create',
			update : 'service/catalog/json/spatialrange/update',
			destroy : 'service/catalog/json/spatialrange/delete'
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