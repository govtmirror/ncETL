Ext.define("ncETL.panel.ModelFormGroup", {
	extend: 'Ext.panel.Panel',
	btns : undefined,
	children : undefined,
	constructor: function(config) {
		if (!config) config = {};
		
		var _editable = !config.isRootNode;
		var _store = config.store;
		var _modelName = config.modelName;
		
		this.btns = new Ext.Container({});
		this.children = new Ext.Container({});
		
		_store.data.each(function(item) {
			this.items.add(new ncETL.form.Model({
				model : item,
				defaults : {
					anchor : '100%'
				}
			}));
		}, {
			items : this.children
		});
        
		
		config = Ext.apply({
			border : false,
			items : [
				this.btns,
				this.children
			]
		}, config);
		ncETL.panel.ModelFormGroup.superclass.constructor.call(this, config);
		
		_store.on('load', function(str, groupers, successful, operation, eOpts) {
			str.data.each(function(item) {
				this.group.add(new ncETL.form.Model({
					model : item
				}));
			}, this);
		},{
			group : this.children
		});
	},
	saveRecords : function() {
		this.children.items.each(function(item){
			if (item.saveRecord) {
				item.saveRecord();
			}
		}, this);
	},
	reload : function() {
		this.children.removeAll();
		this.store.each(function(record) {
			var form = new ncETL.form.Model({
				model : record,
				defaults : {
					anchor : '100%'
				}
			});
			this.children.add(form);
		}, this);
	},
	scope : this
});