Ext.define('Ssp.store.reference.EducationalGoals', {
    extend: 'Ext.data.Store',
    model: 'Ssp.model.reference.EducationalGoal',
    storeId: 'educationalGoalsReferenceStore',
	autoLoad: false,
	autoSync: true,

    proxy: {
		type: 'rest',
		url: '/ssp/api/1/reference/educationGoal/',
		actionMethods: {
			create: "POST", 
			read: "GET", 
			update: "PUT", 
			destroy: "DELETE"
		},
		reader: {
			type: 'json'
		},
        writer: {
            type: 'json',
            successProperty: 'success'
        }
	}	
	
});