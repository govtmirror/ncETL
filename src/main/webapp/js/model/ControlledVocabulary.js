Ext.define('ControlledVocabulary', {
	extend: 'Ext.data.Model',
        // TODO add more associations
        associations: [{
                type : 'belongsTo', 
                model : 'Publisher'
        }],
	fields: [{
		name : 'id',
		type : 'int',
                editor : {
                    xtype : 'hidden'
                }
	}, {
		name : 'vocab',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Vocab',
                    allowBlank : false,
                    name : 'vocab'
                }
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/vocab',
			create : 'service/catalog/json/vocab/create',
			update : 'service/catalog/json/vocab/update',
			destroy : 'service/catalog/json/vocab/delete'
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