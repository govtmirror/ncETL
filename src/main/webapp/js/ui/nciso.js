Ext.define("ncETL.panel.ncISO", {
    extend : 'Ext.panel.Panel',
    filename : undefined,
    constructor: function(config) {
        if (!config) config = {};

        this.filename = config.filename;
        //divArea = document.getElementById("decorateContent");
        Ext.Ajax.request({
            url: 'nciso',
            params: {
                file: this.filename,
                output: 'rubric'
            },
            success: function(response) {

                // element is destroyed when switching datasets, so need to recreate everytime
                //var div = document.getElementById("decorateContent");
                //var el = document.createElement("div");
                //div.insertBefore(el, null);
                //el.innerHTML = response.responseText;
                config.html = response.responseText;
            //                    var rubric = new Ext.Panel({
            //                            title : 'ncISO Rubric',
            //                            layout: 'fit',
            //                            autoScroll: true,
            //                            autoDestroy: false,
            //                            contentEl: el
            //                    });
            },
            failure: function(){
                alert("failure");
            }
        });
        ncETL.panel.ncISO.superclass.constructor.call(this, config);
    }
});