Ext.define("ncETL.panel.ModelForm", {
	extend: 'Ext.panel.Panel',
	constructor: function(config) {
		if (!config) config = {};
		
		var _modelInst = config.model;
		var _itemsArray = [];

		var _saveButton = Ext.getCmp('saveButton');
		if (!_saveButton) {
			_saveButton = new Ext.button.Button({
				text : 'Save Values',
				id : 'saveButton'
			});

			_saveButton.on("click", function() {
				this.items.each(function(item) {
					if (item.saveRecord) {
						item.saveRecord();
					} else if (item.saveRecords) {
						item.saveRecords();
					}

				}, this);
			}, this);
			config = Ext.apply({
				fbar : [ _saveButton ]
			}, config);
		}

		var rootStore = new Ext.data.Store({
			model : _modelInst.modelName,
			data : [_modelInst]
		});

		var _baseForm = new ncETL.panel.ModelFormGroup({
			isRootNode : true,
			modelName : _modelInst.modelName,
			store : rootStore
		});

		_itemsArray.push(_baseForm);
		
		config = Ext.apply({
			items : _itemsArray
		}, config);
                
		ncETL.panel.ModelForm.superclass.constructor.call(this, config);
	}
});