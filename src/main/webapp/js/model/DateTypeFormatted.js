Ext.define('DateTypeFormatted', {
    extend: 'Ext.data.Model',
    belongsTo : 'TimeCoverage',
    fields: [{
        name : 'id',
        type : 'int',
        editor : {
            xtype : 'hidden'
        }
    }, {
        name : 'format',
        type : 'string'
    }, {
        name : 'value',
        type : 'string'
    }, {
        name : 'date_type_enum_id',
        type : 'int'
    }],
    proxy: {
        type : 'spec',
        api : {
            read : 'service/catalog/json/datetype',
            create : 'service/catalog/json/datetype/create',
            update : 'service/catalog/json/datetype/update',
            destroy : 'service/catalog/json/datetype/delete'
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