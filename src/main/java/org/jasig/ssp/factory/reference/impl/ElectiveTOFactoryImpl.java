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
package org.jasig.ssp.factory.reference.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jasig.ssp.dao.reference.ElectiveDao;
import org.jasig.ssp.factory.reference.AbstractReferenceTOFactory;
import org.jasig.ssp.factory.reference.ElectiveTOFactory;
import org.jasig.ssp.model.reference.Color;
import org.jasig.ssp.model.reference.Elective;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.reference.ColorService;
import org.jasig.ssp.service.reference.ElectiveService;
import org.jasig.ssp.service.reference.impl.ColorServiceImpl;
import org.jasig.ssp.transferobject.reference.ColorTO;
import org.jasig.ssp.transferobject.reference.ElectiveTO;

@Service
@Transactional(readOnly = true)
public class ElectiveTOFactoryImpl extends
		AbstractReferenceTOFactory<ElectiveTO, Elective>
		implements ElectiveTOFactory {

	@Autowired
	protected transient ColorService colorService;
	
	public ElectiveTOFactoryImpl() {
		super(ElectiveTO.class, Elective.class);
	}

	@Autowired
	private transient ElectiveDao dao;

	@Override
	protected ElectiveDao getDao() {
		return dao;
	}
	
	@Override
	public Elective from(final ElectiveTO tObject)
			throws ObjectNotFoundException {
		final Elective model = super.from(tObject);
		
		model.setCode(tObject.getCode());
		model.setSortOrder(tObject.getSortOrder());	
		if(tObject.getColor() != null) {
			model.setColor(colorService.get(tObject.getColor()));
		}
		return model;
	}

}
