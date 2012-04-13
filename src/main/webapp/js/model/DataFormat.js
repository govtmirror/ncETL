Ext.define('DataFormat', {
    extend: 'Ext.data.Model',
    belongsTo: 'Access',
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
            read : 'service/lookup/json/dataformat',
            create : 'service/lookup/json/dataformat/create',
            update : 'service/lookup/json/dataformat/update',
            destroy : 'service/lookup/json/dataformat/delete'
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