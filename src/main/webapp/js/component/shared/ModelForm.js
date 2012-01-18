Ext.define("ncETL.form.Model", {
	extend: 'Ext.Panel',
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
		
		var _btns = new Ext.Container({
			layout : {
				type : 'hbox'
			}
		});
        
		_fields.push(_btns);
		
		_model.associations.each(function(item) {
			if (item.type === 'hasMany') {
				var _assocStore = this.parent[item.name]();
				
				var group = new ncETL.panel.ModelFormGroup({
					margin : '0 30 0 30',
					modelName : item.model,
					store : _assocStore
				});
				
				this.items.push(group);
				
				this.btns.add(new Ext.button.Button({
					text : "Add " + item.model,
					handler : function(btn) {
						this.store.add(Ext.ModelMgr.getModel(this.modelName).create());
						this.that.reload();
					},
					scope : {
						that : group, 
						store : _assocStore, 
						modelName : item.model
					}
				}));
			}
		}, {
			group : this,
			btns : _btns,
			items : _children, 
			parent : _model
		});
		
		this.form = new Ext.form.Panel({
			border : false,
			bodyStyle : 'background-color:transparent',
			defaults : {
				anchor : '100%'
			},
			items : _fields
		});
		this.children = new Ext.Container({
			items : _children
		})
		config = Ext.apply({
			frame : true,
			title : _model.modelName,
			items : [
			this.form,
			this.children
			]
		}, config);
		ncETL.form.Model.superclass.constructor.call(this, config);
		
		this.form.loadRecord(_model);
	}
});