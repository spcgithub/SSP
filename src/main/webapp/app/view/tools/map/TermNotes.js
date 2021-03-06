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
Ext.define('Ssp.view.tools.map.TermNotes', {
    extend: 'Ext.window.Window',
    alias: 'widget.termnotes',
    mixins: ['Deft.mixin.Injectable', 'Deft.mixin.Controllable'],
    height: 500,
    width: 500,
    resizable: true,
    initComponent: function() {
		var me=this;
		Ext.apply(me, 
				{
					layout: {
                align: 'stretch',
                type: 'vbox'
            },
            title: 'Term Notes',
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
                bodyPadding: 0,
                autoScroll: true,
                name: 'termnotesForm',
                fieldDefaults: {
                        msgTarget: 'side',
                        labelAlign: 'left',
                        labelWidth: 100
                    },
               
				    items: [
				   {
				        fieldLabel: 'Advisor/Coach Notes',
				        allowBlank:true,
				        name: 'contactNotes',
				        xtype: 'textareafield',
				        autoscroll: true,
				        flex:1
				    },{
				        fieldLabel: 'Student Notes',
				        name: 'studentNotes',
				        allowBlank:true,
				        xtype: 'textareafield',
				        flex:1,
				        autoscroll: true
				    },
				    {
                        name: 'isImportant',
                        inputValue: 'isImportant',
                        xtype:'checkbox',
                        padding: '0 0 0 105',
                        labelSeparator: '',
                        hideLabel: true,
                        boxLabel: 'Mark As Important',
                        fieldLabel: 'Mark As Important' 
                        }]
				    ,
				    dockedItems: [{
		                xtype: 'toolbar',
		                dock: 'top',
		                items: [{
		                    xtype: 'button',
		                    name: 'saveButton',
		                    text: 'Save'
		                    
		                }, '-', {
		                    xtype: 'button',
		                    name: 'cancelButton',
		                    text: 'Cancel',
							listeners:{
								click:function(){
									this.close();
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