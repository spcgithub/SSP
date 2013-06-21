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
Ext.define('Ssp.controller.tool.map.MapPlanToolViewController', {
    extend: 'Deft.mvc.ViewController',
    mixins: [ 'Deft.mixin.Injectable' ],
    inject:{
		appEventsController: 'appEventsController',
		formUtils: 'formRendererUtils',
    	currentMapPlan: 'currentMapPlan',
    	mapPlanService:'mapPlanService',
    },
    
	init: function() {
		var me=this;
	    me.resetForm();
		me.currentMapPlan.addListener();
	    me.getView().loadRecord(me.currentMapPlan);
	   		me.appEventsController.getApplication().addListener("onUpdateCurrentMapPlanPlanToolView", me.onUpdateCurrentMapPlan, me);
		return me.callParent(arguments);
    },
    resetForm: function() {
        var me = this;
        me.getView().getForm().reset();
    },

    updatePlanStatus: function(){
    	me.getView().setLoading(true);
 		var callbacks = new Object();
 		var serviceResponses = {
             failures: {},
             successes: {},
             responseCnt: 0,
             expectedResponseCnt: 1
         }
 		callbacks.success = me.newServiceSuccessHandler('planStatus', me.onPlanStatusSuccess, serviceResponses);
 		callbacks.failure = me.newServiceFailureHandler('planStatus', me.onPlanStatusFailure, serviceResponses);
 		callbacks.scope = me;
 		me.mapPlanService.planStatus(me.currentMapPlan, callbacks);
    },
    
	onUpdateCurrentMapPlan: function(){
		var me = this;
		me.getView().loadRecord(me.currentMapPlan);
		me.onCurrentMapPlanChange();
	},
	
	onCurrentMapPlanChange: function(){
		var me = this;
		me.appEventsController.getApplication().fireEvent("onCurrentMapPlanChangeUpdateMapView");
	},
	
	destroy: function(){
		var me = this;
		me.appEventsController.getApplication().removeListener("onUpdateCurrentMapPlanPlanToolView", me.onUpdateCurrentMapPlan, me);
	}
});
