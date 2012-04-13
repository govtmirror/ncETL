Ext.define('Service', {
	extend: 'Ext.data.Model',
        associations : [{
            type: 'hasMany',
            model: 'Service',
            primaryKey: 'id',
            foreignKey: 'service_id',
            autoLoad: true
        }, {
            type : 'belongsTo',
            model : 'Catalog'
        }, {
            type : 'belongsTo',
            model : 'Service'
        }],
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
		name : 'service_id',
		type : 'int'
	}, {
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
			read : 'service/catalog/json/srvc',
			create : 'service/catalog/json/srvc/create',
			update : 'service/catalog/json/srvc/update',
			destroy : 'service/catalog/json/srvc/delete'
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