Ext.define("ncETL.panel.ModelFormGroup", {
	extend: 'Ext.panel.Panel',
	constructor: function(config) {
		if (!config) config = {};
		
		var _editable = !config.isRootNode;
		var _store = config.store;
		var _modelName = config.modelName;
		
		var _items = [];
		
		if (_editable) {
			_items.push(new Ext.button.Button({
				text : "Add " + _modelName,
				handler : function(btn) {
					this.store.add(Ext.ModelMgr.getModel(this.modelName).create());
					this.that.reload();
				},
				scope : {that : this, store : _store, modelName : _modelName}
			}));
		}
		
		_store.data.each(function(item) {
			this.items.push(new ncETL.form.Model({
				model : item,
				defaults : {
					anchor : '100%'
				}
			}));
		}, {
			items : _items
		});
        
		config = Ext.apply({
			items : _items
		}, config);
		ncETL.panel.ModelFormGroup.superclass.constructor.call(this, config);
	},
	saveRecords : function() {
		this.items.each(function(item){
			if (item.saveRecord) {
				item.saveRecord();
			}
		}, this);
	},
	reload : function() {
		this.store.each(function(record) {
			var form = new ncETL.form.Model({
				model : record,
				defaults : {
					anchor : '100%'
				}
			});
			this.add(form);
		}, this);
	},
	scope : this
});