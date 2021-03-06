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
Ext.define('Ssp.controller.tool.profile.AcademicProgramViewController', {
    extend: 'Deft.mvc.ViewController',
    mixins: ['Deft.mixin.Injectable'],
    inject: {
        apiProperties: 'apiProperties',
        appEventsController: 'appEventsController',
        personLite: 'personLite',
        personService: 'personService',
        sspConfig: 'sspConfig',
		formUtils: 'formRendererUtils',
		mapPlanService: 'mapPlanService',
		currentMapPlan: 'currentMapPlan',
		termsStore: 'termsStore',
		authenticatedPerson: 'authenticatedPerson'
    },
    
    control: {
        academicProgramsField: '#academicPrograms',
        onPlanField: '#onPlan',
        mapNameField: '#mapName',
        advisorField: '#advisor',
        mapLastUpdatedField: '#mapLastUpdated',
        mapProjectedField: '#mapProjected',
    
		'printPlanButton':{
	        selector: '#printPlanButton',
	        listeners: {
	         click: 'onprintPlanButtonClick'
	        }
	     },       
	    'emailPlanButton':{
	       selector: '#emailPlanButton',
	       listeners: {
	        click: 'onemailPlanButtonClick'
	       }
	    }
    
    },
    init: function(){
        var me = this;
        var id = me.personLite.get('id');
        me.resetForm();
        if (id != "") {
            me.getView().setLoading(true);

            var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            }
            
            if(me.termsStore.getTotalCount() <= 0){
            	me.termsStore.addListener('load', me.termsLoaded, me);
            	me.termsStore.load();
            }else{
            	me.fireOnTermsLoad();
            }
        }
        me.appEventsController.assignEvent({eventName: 'onPrintCurrentMapPlan', callBackFunc: me.onPrintCurrentMapPlan, scope: me});
        me.appEventsController.assignEvent({eventName: 'onEmailCurrentMapPlan', callBackFunc: me.onEmailCurrentMapPlan, scope: me});
        return me.callParent(arguments);
    },
    
    termsLoaded: function(){
    	var me = this;
    	me.termsStore.removeListener('load', me.termsLoaded, me);
    	me.fireOnTermsLoad();
    },
    
    fireOnTermsLoad: function(){
	    var me =  this;
		var id = me.personLite.get('id');
		if(id != ""){
			var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            }

    		me.mapPlanService.getCurrent(id, {
            	success: me.newServiceSuccessHandler('map', me.getMapPlanServiceSuccess, serviceResponses),
            	failure: me.newServiceFailureHandler('map', me.getMapPlanServiceFailure, serviceResponses),
            	scope: me
        	});
		}
    },

	onemailPlanButtonClick: function(button){
        var me=this;
		if(me.emailPlanPopUp == null || me.emailPlanPopUp.isDestroyed)
         	me.emailPlanPopUp = Ext.create('Ssp.view.tools.map.EmailPlan',{hidden:true});
		me.emailPlanPopUp.emailEvent = 'onEmailCurrentMapPlan';
		me.emailPlanPopUp.show();
    },
    
    onprintPlanButtonClick: function(button){
       var me=this;
		if(me.printPlanPopUp == null || me.printPlanPopUp.isDestroyed)
			me.printPlanPopUp = Ext.create('Ssp.view.tools.map.PrintPlan',{hidden:true});
		me.printPlanPopUp.printEvent = 'onPrintCurrentMapPlan';
		me.printPlanPopUp.show();
    },

    resetForm: function() {
        var me = this;
        me.getView().getForm().reset();
    },

   newServiceSuccessHandler: function(name, callback, serviceResponses) {
        var me = this;
        return me.newServiceHandler(name, callback, serviceResponses, function(name, serviceResponses, response) {
            serviceResponses.successes[name] = response;
        });
    },

    newServiceFailureHandler: function(name, callback, serviceResponses) {
        var me = this;
        return me.newServiceHandler(name, callback, serviceResponses, function(name, serviceResponses, response) {
            serviceResponses.failures[name] = response;
        });
    },

    newServiceHandler: function(name, callback, serviceResponses, serviceResponsesCallback) {
        return function(r, scope) {
            var me = scope;
            serviceResponses.responseCnt++;
            if ( serviceResponsesCallback ) {
                serviceResponsesCallback.apply(me, [name, serviceResponses, r]);
            }
            if ( callback ) {
                callback.apply(me, [ serviceResponses ]);
            }
            me.afterServiceHandler(serviceResponses);
        };
    },


	getMapPlanServiceSuccess: function(serviceResponses) {
        var me = this;
        var mapResponse = serviceResponses.successes.map;
		if(!mapResponse || !mapResponse.responseText || mapResponse.responseText.trim().length == 0) {
			 me.getOnPlanField().setValue("Plan Does Not Exist.");
			me.getPrintPlanButton().hide();
			me.getEmailPlanButton().hide();
       	} else {
			me.currentMapPlan.loadFromServer(Ext.decode(mapResponse.responseText));
			var lastTerm = me.termsStore.getTermsFromTermCodes(me.mapPlanService.getTermCodes(me.currentMapPlan))[0];
	        me.getOnPlanField().setValue("Plan Exists.")
	        me.getMapNameField().setValue(me.currentMapPlan.get("name"));
	        me.getMapLastUpdatedField().setValue(me.currentMapPlan.getFormattedModifiedDate());
			if(lastTerm)
	        	me.getMapProjectedField().setValue(lastTerm.get("code"));
	        me.getPrintPlanButton().show();
			me.getEmailPlanButton().show();
			me.updatePlanStatus();
		}
    },

    getMapPlanServiceFailure: function() {
    	var me = this;
		me.getPrintPlanButton().hide();
		me.getEmailPlanButton().hide();
    },
    
    getPersonSuccess: function(serviceResponses) {
        var me = this;
        var person = serviceResponses.successes.person;
		if(!person) {
			return;
       	} else {
       		var personResponse = serviceResponses.successes.person;
       		var advisor = new Ssp.model.Person();
       		advisor.populateFromGenericObject(personResponse);
	        me.getAdvisorField().setValue(advisor.getFullName());
		}
    },

    getPersonFailure: function() {

    },

    afterServiceHandler: function(serviceResponses) {
        var me = this;
        if ( serviceResponses.responseCnt >= serviceResponses.expectedResponseCnt ) {
            me.getView().setLoading(false);
        }
    },

	onEmailCurrentMapPlan: function(metaData){
		var me = this;
		me.getView().setLoading(true);
		var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            };
		me.mapPlanService.email(null, metaData, {
            success: me.newServiceSuccessHandler('emailMap', me.emailMapPlanServiceSuccess, serviceResponses),
            failure: me.newServiceFailureHandler('emailMap', me.emailMapPlanServiceFailure, serviceResponses),
            scope: me,
            isPrivate: true
        });
	},
	
	 emailMapPlanServiceSuccess: function(serviceResponses) {
	        var me = this;
	        var mapResponse = serviceResponses.successes.emailMap;
	       	me.onEmailComplete(mapResponse.responseText);
			me.getView().setLoading(false);
	 },

	emailMapPlanServiceFailure: function() {
		var me = this;
		me.getView().setLoading(false);
	},
	
	onEmailComplete: function(responseText){
		Ext.Msg.alert('SSP Email Service', responseText);
	},
	
	onPrintCurrentMapPlan: function(metaData){
		var me = this;
		me.getView().setLoading(true);
		var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            }
		me.mapPlanService.print(null, metaData, {
            success: me.newServiceSuccessHandler('printMap', me.printMapPlanServiceSuccess, serviceResponses),
            failure: me.newServiceFailureHandler('printMap', me.printMapPlanServiceFailure, serviceResponses),
            scope: me,
            isPrivate: true
        });
	},
	
	 printMapPlanServiceSuccess: function(serviceResponses) {
	        var me = this;
	        var mapResponse = serviceResponses.successes.printMap;
	       	me.onPrintComplete(mapResponse.responseText);
			me.getView().setLoading(false);
	 },

	printMapPlanServiceFailure: function() {
		var me = this;
		me.getView().setLoading(false);
	},
	
	onPrintComplete: function(htmlPrint){
    	var targetElement = Ext.getCmp('PrintablePanelId');
        var myWindow = window.open('', '', 'width=500,height=600,scrollbars=yes');
        myWindow.document.write(htmlPrint);
        myWindow.print();
	},
	
	updatePlanStatus: function(){
		var me=this;
    	if(me.currentMapPlan.get('isTemplate') == true || me.currentMapPlan.get('personId') == ""){
    		me.getOnPlanField().setValue("");
    		return;
    	}
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
    

	onPlanStatusSuccess:function(serviceResponses){
		var me = this;
		me.getView().setLoading(false);
		var planStatus = serviceResponses.successes.planStatus;
		if(planStatus.responseText && planStatus.responseText.length > 1)
		   planStatus = Ext.decode(planStatus.responseText);
		else
			planStatus = null;
		if(planStatus && planStatus.status == "ON")
			me.getOnPlanField().setValue("On Plan");
		else if(planStatus && planStatus.status == "OFF")
			me.getOnPlanField().setValue("Off Plan");
		else
			me.getOnPlanField().setValue("No Status");
		var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            };
		me.personService.get(me.currentMapPlan.get('ownerId'), {
            success: me.newServiceSuccessHandler('person', me.getPersonSuccess, serviceResponses),
            failure: me.newServiceFailureHandler('person', me.getPersonFailure, serviceResponses),
            scope: me
        });
	},
	
	onPlanStatusFailure:function(){
		var me = this;
		me.getView().setLoading(false);
		me.getOnPlanField().setValue("No Status");
		var serviceResponses = {
                failures: {},
                successes: {},
                responseCnt: 0,
                expectedResponseCnt: 1
            };
		me.personService.get(me.currentMapPlan.get('ownerId'), {
            success: me.newServiceSuccessHandler('person', me.getPersonSuccess, serviceResponses),
            failure: me.newServiceFailureHandler('person', me.getPersonFailure, serviceResponses),
            scope: me
        });
	},
	

	destroy: function() {
        var me=this;
		me.appEventsController.removeEvent({eventName: 'onPrintCurrentMapPlan', callBackFunc: me.onPrintCurrentMapPlan, scope: me});
		me.appEventsController.removeEvent({eventName: 'onEmailCurrentMapPlan', callBackFunc: me.onEmailCurrentMapPlan, scope: me});
		if(me.emailPlanPopUp != null && !me.emailPlanPopUp.isDestroyed)
	    	me.emailPlanPopUp.close();
		if(me.printPlanPopUp != null && !me.printPlanPopUp.isDestroyed)
	    	me.printPlanPopUp.close();
        return me.callParent( arguments );
    }
});
