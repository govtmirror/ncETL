Ext.define('Creator', {
	extend: 'Ext.data.Model',
        belongsTo: 'Dataset',
        // might want ControlledVocabulary as association
	fields: [{
		name : 'id',
		type : 'int',
                editor : {
                    xtype : 'hidden'
                }
	}, {
		name : 'name',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Name',
                    allowBlank : false,
                    name : 'name'
                }
	}, {
		name : 'controlled_vocabulary_id',
		type : 'int',
                editor : {
                    xtype: 'combo',
                    fieldLabel : 'Vocabulary',
                    store : new Ext.data.Store({
                        model : 'ControlledVocabulary',
                        autoLoad : true
                    }),
                    queryMode: 'local',
                    displayField: 'type',
                    valueField: 'id',
                    name : 'controlled_vocabulary_id',
                    triggerAction : 'all',
                    typeAhead : true,
                    forceSelection : true
                }
	}, {
		name : 'contact_url',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Contact URL',
                    allowBlank : false,
                    name : 'contact_url'
                }
	}, {
		name : 'contact_email',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Email',
                    allowBlank : false,
                    name : 'contact_email'
                }
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/creator/json/default',
			create : 'service/creator/json/default/create',
			update : 'service/creator/json/default/update',
			destroy : 'service/creator/json/default/delete'
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