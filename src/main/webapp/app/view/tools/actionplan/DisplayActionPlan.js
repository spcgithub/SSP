Ext.define('Ssp.view.tools.actionplan.DisplayActionPlan', {
	extend: 'Ext.panel.Panel',
	alias : 'widget.displayactionplan',
    mixins: [ 'Deft.mixin.Injectable',
              'Deft.mixin.Controllable'],
    controller: 'Ssp.controller.tool.actionplan.DisplayActionPlanViewController',
    inject: {
    	person: 'currentPerson'
    },
    width: '100%',
	height: '100%',
	initComponent: function() {	
		Ext.apply(this, 
				{
		            title: 'Action Plan',
		            autoScroll: true,
		            padding: 0,
					items: [
						Ext.createWidget('tabpanel', {
						    width: '100%',
						    height: '100%',
						    activeTab: 0,
						    itemId: 'taskStatusTabs',
						    items: [{ 
						    	      title: 'Active',
						    		  autoScroll: true,
						    		  action: 'active',
						    		  items: [{xtype: 'tasks'}]
						    		},{ 
						    		  title: 'Complete',
						    		  autoScroll: true,
						    		  action: 'complete',
						    		  items: [{xtype: 'tasks'}]
						    		},{ 
						    		  title: 'All',
						    		  autoScroll: true,
						    		  action: 'all',
						    		  items: [{xtype: 'tasks'}]
						    		}]
						})
						,{xtype: 'displayactionplangoals', flex: 1}
						,{xtype: 'displaystrengths'}
				    ],
				    
				    dockedItems: [{
				        dock: 'top',
				        xtype: 'toolbar',
				        items: [{
				            xtype: 'checkbox',
				            boxLabel: 'Display only tasks that I created',
				            itemId: 'filterTasksBySelfCheck'
				           }]
				    },{
				        dock: 'bottom',
				        xtype: 'toolbar',
				        items: [{
				            tooltip: 'Email Action Plan',
				            text: 'Email',
				            xtype: 'button',
				            itemId: 'emailTasksButton'
				        },{
				            tooltip: 'Print Action Plan',
				            text: 'Print',
				            xtype: 'button',
				            itemId: 'printTasksButton'
				        },{ 
				        	xtype: 'tbspacer',
				        	flex: 1
				        },{
				            tooltip: 'View Student History',
				            text: 'View History',
				            xtype: 'button',
				            itemId: 'viewHistoryButton'
				        }]
				    }]
				});
	
		return this.callParent(arguments);
	}
		
});