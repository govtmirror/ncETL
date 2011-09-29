Ext.define("ncETL.panel.ModelFormGroup", {
	extend: 'Ext.panel.Panel',
	constructor: function(config) {
		if (!config) config = {};
		
		var _store = config.store;
		
		var _items = [];
		
		_store.on('load', function(store, records, successful, operation, options) {
			this.reload();
		}, this);
		
        _items.push(new Ext.button.Button({
            text : "Add " + _store.model.getName(), // TODO is this kosher Sibley?
            handler : function(record) {
				var form = new ncETL.form.Model({
					model : this.store.model.getName(),
					defaults : {
						anchor : '100%'
					}
				});
				this.store.add(record);
                this.reload(this.store);
			},
            scope : this
        }));
        
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
					model : record.self.getName(),
					defaults : {
						anchor : '100%'
					}
				});
				form.loadRecord(record);
				this.add(form);
			}, this);
    }
});