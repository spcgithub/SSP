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
Ext.define('Ssp.controller.tool.journal.DisplayDetailsViewController', {
    extend: 'Deft.mvc.ViewController',
    mixins: [ 'Deft.mixin.Injectable' ],
    inject: {
    	appEventsController: 'appEventsController',
    	model: 'currentJournalEntry',
    	store: 'journalEntryDetailsStore'
    },   
	
    control: {
		view: {
			viewready: 'onViewReady'
		}
	},
	
    init: function() {
    	var me=this;
		me.store.loadData( me.model.getGroupedDetails() );		
		return me.callParent( arguments );
    },
    
    onViewReady: function(){
    	this.appEventsController.assignEvent({eventName: 'refreshJournalEntryDetails', callBackFunc: this.onRefreshJournalEntryDetails, scope: this});
    },
    
    destroy: function(){
    	this.appEventsController.removeEvent({eventName: 'refreshJournalEntryDetails', callBackFunc: this.onRefreshJournalEntryDetails, scope: this});    	
    },
    
    onRefreshJournalEntryDetails: function(){
    	this.init();
    }
});