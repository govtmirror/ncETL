Ext.define('Contributor', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
	fields: [{
		name : 'id',
		type : 'int',
                editor : {
                    xtype : 'hidden'
                }
	}, {
		name : 'role',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Role',
                    allowBlank : false,
                    name : 'role'
                }
	}, {
		name : 'text',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Text',
                    allowBlank : false,
                    name : 'text'
                }
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/catalog/json/contributor',
			create : 'service/catalog/json/contributor/create',
			update : 'service/catalog/json/contributor/update',
			destroy : 'service/catalog/json/contributor/delete'
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