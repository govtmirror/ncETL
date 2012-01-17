Ext.define('Service', {
	extend: 'Ext.data.Model',
        belongsTo: 'Catalog',
	fields: [{
		name : 'id',
		type : 'int'
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
		name : 'catalog_id',
		type : 'int'
	}, {
//		name : 'service_id',
//		type : 'int'
//	}, {
		name : 'service_type_id',
		type : 'int',
                editor : {
                    xtype: 'combo',
                    fieldLabel : 'Service Type',
                    store : new Ext.data.Store({
                        model : 'ServiceType',
                        autoLoad : true
                    }),
                    queryMode: 'local',
                    displayField: 'type',
                    valueField: 'id',
                    name : 'service_type_id',
                    triggerAction : 'all',
                    typeAhead : true,
                    forceSelection : true
                }
	}, {
		name : 'base',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Base',
                    allowBlank : false,
                    name : 'base'
                }
	}, {
		name : 'description',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Description',
                    allowBlank : true,
                    name : 'description'
                }
	}, {
		name : 'suffix',
		type : 'string',
                editor : {
                    xtype: 'textfield',
                    fieldLabel : 'Suffix',
                    allowBlank : true,
                    name : 'suffix'
                }
	}],
	proxy: {
		type : 'spec',
		api : {
			read : 'service/srvc/json/default',
			create : 'service/srvc/json/default/create',
			update : 'service/srvc/json/default/update',
			destroy : 'service/srvc/json/default/delete'
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