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
Ext.define('Ssp.view.tools.map.CourseNotes', {
    extend: 'Ext.window.Window',
    alias: 'widget.coursenotes',
    mixins: ['Deft.mixin.Injectable', 'Deft.mixin.Controllable'],
	inject: {
		electiveStore : 'electivesAllUnpagedStore',
	    formUtils: 'formRendererUtils',
    	currentMapPlan: 'currentMapPlan'
	},
    height: 390,
    width: 480,
    resizable: true,
    parentGrid: null,
    enableFields: true,
    initComponent: function() {
		var me=this;
		Ext.apply(me, 
				{
					layout: {
                align: 'stretch',
                type: 'vbox'
            },
            title: 'Course Notes',
            items:[{
                xtype: 'form',
                flex: 1,
                border: 0,
                frame: false,
                layout: {
                    align: 'stretch',
                    type: 'vbox'
                },
                width: '100%',
                height: '100%',
                bodyPadding: 10,
                autoScroll: true,
                itemId: 'coursenotesForm',
                fieldDefaults: {
                        msgTarget: 'side',
                        labelAlign: 'left',
                        labelWidth: 100
                    },
               
				    items: [
				   {
				        fieldLabel: 'Advisor/Coach Notes',
				        name: 'contactNotes',
				        allowBlank:true,
				        xtype: 'textareafield',
				        itemId: 'contactNotes',
				        autoscroll: true,
				        disabled: !me.enableFields && !me.currentMapPlan.get('isTemplate'),
				        flex:1
				    },{
				        fieldLabel: 'Student Notes',
				        name: 'studentNotes',
				        allowBlank:true,
				        itemId: 'studentNotes',
				        xtype: 'textareafield',
				        flex:1,
				        disabled: !me.enableFields && !me.currentMapPlan.get('isTemplate'),
				        autoscroll: true
				    },
				    {
				    	xtype: 'numberfield',
				        anchor: '100%',
				        maxValue: 99,
				        allowDecimals: true,
				        minValue: 0,
				        step: 1,
				        fieldLabel: 'Credit hours(over ride)',
				        name: 'creditHours',
				        allowBlank:true,
				        itemId: 'creditHours',
				        disabled: !me.enableFields && !me.currentMapPlan.get('isTemplate')
				        //flex:1,
				        
				    },
				    {
                    	name: 'isImportant',
						itemId: 'isImportant',
                    	inputValue: 'isImportant',
                    	xtype:'checkbox',
                    	padding: '0 0 0 105',
                    	labelSeparator: '',
                    	hideLabel: true,
                    	boxLabel: 'Mark As Important',
                    	fieldLabel: 'Mark As Important',
				        disabled: !me.enableFields && !me.currentMapPlan.get('isTemplate')

                    },
                    {
                        xtype: 'combobox',
                        itemId: 'electiveId',
						name: 'electiveId',
                        store: me.electiveStore,
                        fieldLabel: '',
                        emptyText: 'Elective',
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'local',
                        typeAhead: true,
                        allowBlank: true,
                        queryMode: 'local',
                        width: 250,
				        disabled: !me.enableFields && !me.currentMapPlan.get('isTemplate'),
				        associativeField: 'id'
                    }
				    ]
				    ,
				    dockedItems: [{
		                xtype: 'toolbar',
		                dock: 'top',
		                items: [{
		                    xtype: 'button',
		                    itemId: 'saveButton',
		                    text: 'Save',
		                    listeners: {
		                    	click:function(){
		                    		me = this;
									var mapCourse = me.semesterStore.getAt(me.rowIndex);
									me.query('form')[0].getForm().updateRecord(mapCourse);
		                    		me.close();
		                    	},
		                    	scope: me
		                    }
		                    
		                }, '-', {
		                    xtype: 'button',
		                    itemId: 'cancelButton',
		                    text: 'Cancel',
		                    
		                    listeners: {
		                    	click:function(){
		                    		me = this;
		                    		me.close();
		                    	},
		                    	scope: me
		                    }
		                }]
		            
		            }]
		            }]
				});
		
		return me.callParent(arguments);
	}
});