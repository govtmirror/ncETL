Ext.define('DataFormat', {
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
            read : 'service/dataformat/json/default',
            create : 'service/dataformat/json/default/create',
            update : 'service/dataformat/json/default/update',
            destroy : 'service/dataformat/json/default/delete'
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