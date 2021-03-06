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
Ext.define('Ssp.model.reference.JournalTrack', {
	extend: 'Ssp.model.reference.AbstractReference',
    fields: [
	{name: 'objectStatus', type: 'string', defaultValue: 'ACTIVE', convert: function(value, record){
                 // 'objectStatus' is part of the back-end API, so handling is
                 // similar but slightly different than 'active' b/c we do
                 // trust 'value' during both initialization and field-to-field
                 // syncs.
                 if ( !(record.statusFieldsInitialized) || record.synchronizingStatusFields ) {
                     
                     return value;
                 }
                 record.synchronizingStatusFields = true;
                 record.set('active', 'ACTIVE' === (value && value.toUpperCase()));
                 record.synchronizingStatusFields = false;
                 return value;
             }}]
});