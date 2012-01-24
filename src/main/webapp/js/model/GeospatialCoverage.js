Ext.define('GeospatialCoverage', {
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
        name : 'controlled_vocabulary_id',
        type : 'int'
    }, {
        name : 'zpositive_id',
        type : 'int'
    }],
    proxy: {
        type : 'spec',
        api : {
            read : 'service/catalog/json/geo',
            create : 'service/catalog/json/geo/create',
            update : 'service/catalog/json/geo/update',
            destroy : 'service/catalog/json/geo/delete'
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