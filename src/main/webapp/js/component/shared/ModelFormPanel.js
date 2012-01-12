Ext.define("ncETL.panel.ModelForm", {
    extend: 'Ext.panel.Panel',
    constructor: function(config) {
        if (!config) config = {};
		
        var buildItems = function(modelInst) {
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
                        } else {
                            item.saveRecords();
                        }
					
                    }, this);
                }, this);
                config = Ext.apply({
                    fbar : [ _saveButton ]
                }, config);
            }
                        
            var _model = Ext.ModelManager.getModel(modelInst.modelName).create();
            var _baseForm = new ncETL.panel.ModelFormGroup({
                model : _model
            });
                            
            //                            
            //                            new ncETL.form.Model({
            //				model : modelInst.self.getName(),
            //				defaults : {
            //					anchor : '100%'
            //				}
            //			});
            //_baseForm.reload(); //loadRecord(modelInst);
			
            _itemsArray.push(_baseForm);
                        
                        
            //			
            //			var buildItemsAux = function(array, mInst) {
            //				
            //				mInst.associations.each(function(association) {
            //					if (association.type === 'hasMany') { // TODO association.profile === 'dynamic'
            //						array.push(new ncETL.panel.ModelFormGroup({
            //							store : mInst[association.name]()
            //						}));
            //					}
            //				});
            //			};
			
            //			buildItemsAux(_itemsArray, modelInst);
            //			
            return _itemsArray;
        };
		
        var _modelInst = config.model;
		
        var _items = buildItems(_modelInst);
		

                
        config = Ext.apply({
            items : _items
        }, config);
                
        ncETL.panel.ModelForm.superclass.constructor.call(this, config);
    }
});