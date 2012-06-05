Ext.define('Ssp.store.reference.CampusEarlyAlertRoutings', {
    extend: 'Ssp.store.reference.AbstractReferences',
    model: 'Ssp.model.reference.CampusEarlyAlertRouting',
    constructor: function(){
    	this.callParent(arguments);
    	Ext.apply(this.getProxy(),{url: this.getProxy().url + this.apiProperties.getItemUrl('campusEarlyAlertRouting')});
    }
});