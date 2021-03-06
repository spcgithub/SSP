/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
Ext.define('Ssp.view.tools.actionplan.Tasks', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.tasks',
    mixins: [ 'Deft.mixin.Injectable',
              'Deft.mixin.Controllable'],
    controller: 'Ssp.controller.tool.actionplan.TasksViewController',
    inject: {
    	appEventsController: 'appEventsController',
    	authenticatedPerson: 'authenticatedPerson',
    	columnRendererUtils: 'columnRendererUtils',
    	model: 'currentTask',
        store: 'tasksStore'
    },
    layout: 'auto',
	width: '100%',
    height: '100%',
	minHeight: 200,
	dueDateMsg: 'Task due dates are always interpreted in the institution\'s time zone.',
	dueDateRenderer: function() {
		var me = this;
		return function(value,metaData,record) {
			// http://www.sencha.com/forum/showthread.php?179016
			metaData.tdAttr = 'data-qtip="' + me.dueDateMsg + '"';
			return me.columnRendererUtils.renderTaskDueDate(value,metaData,record);
		}
	},
    initComponent: function(){
    	var me=this;
    	var sm = Ext.create('Ext.selection.CheckboxModel');
    	
    	Ext.apply(me,
    			{
    		        scroll: 'vertical',
    	    		store: me.store,    		
    	    		selModel: sm,
    	    		features: [{
		    	        id: 'group',
		    	        ftype: 'grouping',
		    	        groupHeaderTpl: '{name}',
		    	        depthToIndent: 0,
		    	        hideGroupedHeader: false,
		    	        enableGroupingMenu: false
		    	    }],
		
		    	    columns: [{
		    	        xtype:'actioncolumn',
		    	        width:65,
		    	        header: 'Action',
		    	        items: [{
		    	            icon: Ssp.util.Constants.GRID_ITEM_EDIT_ICON_PATH,
		    	            tooltip: 'Edit Task',
		    	            handler: function(grid, rowIndex, colIndex) {
		    	            	var rec = grid.getStore().getAt(rowIndex);
		    	            	var panel = grid.up('panel');
		    	                panel.model.data=rec.data;
		    	            	panel.appEventsController.getApplication().fireEvent('editTask');
		    	            },
		    	            getClass: function(value, metadata, record)
                            {
		    	            	// completed items cannot be edited 
		    	            	// hide if completed or if user does not have permission to edit
		    	            	var cls = 'x-hide-display';
		    	            	if ( me.authenticatedPerson.hasAccess('EDIT_TASK_BUTTON') && record.get('completedDate') == null)
		    	            	{
		    	            		cls = Ssp.util.Constants.GRID_ITEM_CLOSE_ICON_PATH;
		    	            	}
		    	            	
		    	            	return cls;                            
		    	            },
		    	            scope: me
		    	        },{
		    	            icon: Ssp.util.Constants.GRID_ITEM_CLOSE_ICON_PATH,
		    	            tooltip: 'Close Task',
		    	            handler: function(grid, rowIndex, colIndex) {
		    	            	var rec = grid.getStore().getAt(rowIndex);
		    	            	var panel = grid.up('panel');
		    	                panel.model.data=rec.data;
		    	            	panel.appEventsController.getApplication().fireEvent('closeTask');
		    	            },
		    	            getClass: function(value, metadata, record)
                            {
		    	            	// completed items cannot be closed 
		    	            	// hide if completed or if user does not have permission to edit
		    	            	var cls = 'x-hide-display';
		    	            	if ( me.authenticatedPerson.hasAccess('CLOSE_TASK_BUTTON') && record.get('completedDate') == null)
		    	            	{
		    	            		cls = Ssp.util.Constants.GRID_ITEM_CLOSE_ICON_PATH;
		    	            	}
		    	            	
		    	            	return cls;
		    	            },
		    	            scope: me
		    	        },{
		    	            icon: Ssp.util.Constants.GRID_ITEM_DELETE_ICON_PATH,
		    	            tooltip: 'Delete Task',
		    	            handler: function(grid, rowIndex, colIndex) {
		    	            	var rec = grid.getStore().getAt(rowIndex);
		    	            	var panel = grid.up('panel');
		    	                panel.model.data=rec.data;
		    	            	panel.appEventsController.getApplication().fireEvent('deleteTask');
		    	            },
		    	            getClass: function(value, metadata, record)
                            {
		    	            	// completed items cannot be deleted 
		    	            	// hide if completed or if user does not have permission to delete
		    	            	var cls = 'x-hide-display';
		    	            	if ( me.authenticatedPerson.hasAccess('DELETE_TASK_BUTTON') && record.get('completedDate') == null)
		    	            	{
		    	            		cls = Ssp.util.Constants.GRID_ITEM_CLOSE_ICON_PATH;
		    	            	}
		    	            	
		    	            	return cls;
                            },
		    	            scope: me
		    	        }]
		    	    },{
		    	        text: 'Description',
		    	        flex: 1,
		    	        tdCls: 'task',
		    	        sortable: true,
		    	        dataIndex: 'name',
		    	        renderer: me.columnRendererUtils.renderTaskName
		    	    },{
		    	        header: 'Due Date',
		    	        width: 150,
		    	        dataIndex: 'dueDate',
		    	        renderer: me.dueDateRenderer(),
						listeners: {
							render: function(field){
								Ext.create('Ext.tip.ToolTip',{
									target: field.getEl(),
									html: me.dueDateMsg
								});
							}
						}
		    	    }]
    	

    			});
    	
    	return me.callParent(arguments);
    }
});