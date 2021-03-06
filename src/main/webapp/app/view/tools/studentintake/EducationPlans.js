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
Ext.define('Ssp.view.tools.studentintake.EducationPlans', {
	extend: 'Ext.form.Panel',
	alias: 'widget.studentintakeeducationplans',
	id : 'StudentIntakeEducationPlans',   
    mixins: [ 'Deft.mixin.Injectable',
              'Deft.mixin.Controllable'],
    controller: 'Ssp.controller.tool.studentintake.EducationPlansViewController',
    inject: {
    	formUtils: 'formRendererUtils',
        studentStatusesStore: 'studentStatusesStore'
    },
	width: '100%',
    height: '100%',
	
    initComponent: function() {	
    	var me=this;
		Ext.apply(this, 
				{
					autoScroll: true,
				    layout: {
				    	type: 'vbox',
				    	align: 'stretch'
				    },
				    border: 0,
				    defaults: {
				        anchor: '100%'
				    },
				    fieldDefaults: {
				        msgTarget: 'side',
				        labelAlign: 'left',
				        labelWidth: 225
				    },
				    defaultType: 'displayfield',
				    items: [{
				            xtype: 'fieldset',
							border: 0,
							padding: 10,
				            title: '',
				            defaultType: 'textfield',
				            defaults: {
				                anchor: '95%'
				            },
				       items: [{
				        xtype: 'combobox',
				        name: 'studentStatusId',
				        itemId: 'studentStatusCombo',
				        fieldLabel: 'Student Status',
				        emptyText: 'Select One',
				        store: me.studentStatusesStore,
				        valueField: 'id',
				        displayField: 'name',
				        mode: 'local',
				        typeAhead: true,
				        queryMode: 'local',
				        allowBlank: true
					},{
				        xtype: 'checkboxgroup',
				        fieldLabel: 'Check all that you have completed',
				        columns: 1,
				        items: [
				            {boxLabel: 'New Student Orientation', name: 'newOrientationComplete'},
				            {boxLabel: 'Registered for Classes', name: 'registeredForClasses'}
				        ]
				    },{
				        xtype: "radiogroup",
				        fieldLabel: "Have your parents obtained a college degree?",
				        columns: 1,
				        itemId: "collegeDegreeForParents",
				        items: [
				            {boxLabel: "Yes", itemId: "collegeDegreeForParentsCheckOn", name: "collegeDegreeForParents", inputValue:true},
				            {boxLabel: "No", itemId: "collegeDegreeForParentsCheckOff", name: "collegeDegreeForParents", inputValue:false}]
				    },{
				        xtype: "radiogroup",
				        fieldLabel: "Require special accommodations?",
				        columns: 1,
				        itemId: "specialNeeds",
				        items: [
				            {boxLabel: "Yes", itemId: "specialNeedsCheckOn", name: "specialNeeds", inputValue:"y"},
				            {boxLabel: "No", itemId: "specialNeedsCheckOff", name: "specialNeeds", inputValue:"n"}]
				    },{
				        xtype: 'radiogroup',
				        fieldLabel: 'What grade did you typically earn at your highest level of education?',
				        columns: 1,
				        items: [
				            {boxLabel: 'A', name: 'gradeTypicallyEarned', inputValue: "A"},
				            {boxLabel: 'A-B', name: 'gradeTypicallyEarned', inputValue: "AB"},
				            {boxLabel: 'B', name: 'gradeTypicallyEarned', inputValue: "B"},
				            {boxLabel: 'B-C', name: 'gradeTypicallyEarned', inputValue: "BC"},
				            {boxLabel: 'C', name: 'gradeTypicallyEarned', inputValue: "C"},
				            {boxLabel: 'C-D', name: 'gradeTypicallyEarned', inputValue: "CD"},
				            {boxLabel: 'D', name: 'gradeTypicallyEarned', inputValue: "D"},
				            {boxLabel: 'D-F', name: 'gradeTypicallyEarned', inputValue: "DF"},
				            {boxLabel: 'F', name: 'gradeTypicallyEarned', inputValue: "F"}
				    		]
				    }]
				    }]
				});
		
		return me.callParent(arguments);
	}	
});