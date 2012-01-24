Ext.define('Dataset', {
    extend: 'Ext.data.Model',
    belongsTo: 'Catalog',
    associations : [{
        type: 'hasMany',
        model: 'Access',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Documentation',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Property',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'GeospatialCoverage',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'TimeCoverage',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Contributor',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Creator',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Datatype',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Keyword',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Project',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'Publisher',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }, {
        type: 'hasMany',
        model: 'SpatialRange',
        primaryKey: 'id',
        foreignKey: 'dataset_id',
        autoLoad: true
    }
],
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
        name : 'catalog_id',
        type : 'int'
    }, {
        name : 'data_type_id',
        type : 'int',
        editor : {
            xtype: 'combo',
            fieldLabel : 'Datatype',
            store : new Ext.data.Store({
                model : 'Datatype',
                autoLoad : true
            }),
            queryMode: 'local',
            displayField: 'type',
            valueField: 'id',
            name : 'data_type_id',
            triggerAction : 'all',
            typeAhead : true,
            forceSelection : true
        }
    }, {
        name : 'ncid',
        type : 'string',
        editor : {
            xtype: 'textfield',
            fieldLabel : 'ID',
            allowBlank : false,
            name : 'ncid'
        }
    }, {
        name : 'authority',
        type : 'string',
        editor : {
            xtype: 'textfield',
            fieldLabel : 'Authority',
            allowBlank : false,
            name : 'authority'
        }
    }],
    proxy: {
        type : 'spec',
        api : {
            read : 'service/dataset/json/default',
            create : 'service/dataset/json/default/create',
            update : 'service/dataset/json/default/update',
            destroy : 'service/dataset/json/default/delete'
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