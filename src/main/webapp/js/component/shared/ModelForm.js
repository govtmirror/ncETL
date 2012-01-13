Ext.define("ncETL.form.Model", {
	extend: 'Ext.Container',
	form : undefined,
	children : undefined,
	saveRecord: function() {
		var me = this.form;
		
		var rec = me.getRecord();
		var form = me.getForm();
		
		form.updateRecord(rec);
		
		if (rec.dirty) {
			rec.save();
		}
		
		this.children.items.each(function(item){
			if (item.saveRecord) {
				item.saveRecord();
			} else if (item.saveRecords) {
				item.saveRecords();
			}
		}, this);
	},
	constructor : function(config) {
		if (!config) config = {};
		
		var _model = config.model;
		
		var _fields = [];
		var _children = [];
		
		_model.fields.each(function(item) {
			var _editor = item.editor;
			var _itemName = item.name;
			if (_editor) {
				_fields.push(_editor);
			} else if (_itemName){
				_fields.push({
					xtype: 'displayfield',
					fieldLabel: _itemName,
					name : _itemName
				});
			}
		}, this);
        
		_model.associations.each(function(item) {
			if (item.type === 'hasMany') {
				var _assocStore = this.parent[item.name]();
				
				this.items.push(new ncETL.panel.ModelFormGroup({
					modelName : item.model,
					store : _assocStore
				}));
			}
		}, {
			group : this, 
			items : _children, 
			parent : _model
		});
		
		this.form = new Ext.form.Panel({
			title : _model.modelName,
			items : _fields
		});
		this.children = new Ext.Container({
			items : _children
		})
		config = Ext.apply({
			items : [
			this.form,
			this.children
			]
		}, config);
		ncETL.form.Model.superclass.constructor.call(this, config);
		
		this.form.loadRecord(_model);
	}
});