Ext.define('Datatype', {
    belongsTo: 'Dataset',      
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
            read : 'service/catalog/json/datatype'
        },
        reader : {
            type : 'spec',
            idProperty : 'id'
        },
        listeners : {
            "exception" : function(proxy, response, operation, options) {
            //				console.log("Proxy Exception");
            }
        }
    }
});