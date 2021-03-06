/**
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
package org.jasig.ssp.transferobject;

import org.jasig.ssp.model.PersonSearchResult2;

/**
 * PersonSearchResult transfer object
 */
public class PersonSearchResult2TO extends PersonSearchResult2 implements
		TransferObject<PersonSearchResult2> {

	public PersonSearchResult2TO(final PersonSearchResult2 model) {
		super();
		from(model);
	}


	@Override
	public final void from(final PersonSearchResult2 model) {
		setFirstName(model.getFirstName());
		setLastName(model.getLastName());
		setMiddleName(model.getMiddleName());
		setSchoolId(model.getSchoolId());
		setId(model.getId());
		setCoachId(model.getCoachId());
		setPhotoUrl(model.getPhotoUrl());
		setCoachFirstName(model.getCoachFirstName());
		setCoachLastName(model.getCoachLastName());
		setCurrentProgramStatusName(model.getCurrentProgramStatusName());
	}
}