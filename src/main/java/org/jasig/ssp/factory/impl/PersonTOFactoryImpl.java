package org.jasig.ssp.factory.impl;

import org.jasig.ssp.dao.PersonDao;
import org.jasig.ssp.factory.AbstractAuditableTOFactory;
import org.jasig.ssp.factory.PersonTOFactory;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.transferobject.PersonTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersonTOFactoryImpl extends
		AbstractAuditableTOFactory<PersonTO, Person>
		implements PersonTOFactory {

	public PersonTOFactoryImpl() {
		super(PersonTO.class, Person.class);
	}

	@Autowired
	private transient PersonDao dao;

	@Autowired
	private transient PersonService personService;

	@Override
	protected PersonDao getDao() {
		return dao;
	}

	@Override
	public Person from(final PersonTO tObject)
			throws ObjectNotFoundException {
		final Person model = super.from(tObject);

		model.setFirstName(tObject.getFirstName());
		model.setMiddleInitial(tObject.getMiddleInitial());
		model.setLastName(tObject.getLastName());
		model.setBirthDate(tObject.getBirthDate());
		model.setPrimaryEmailAddress(tObject.getPrimaryEmailAddress());
		model.setSecondaryEmailAddress(tObject.getSecondaryEmailAddress());
		model.setUsername(tObject.getUsername());
		model.setUserId(tObject.getUserId());
		model.setHomePhone(tObject.getHomePhone());
		model.setWorkPhone(tObject.getWorkPhone());
		model.setCellPhone(tObject.getCellPhone());
		model.setAddressLine1(tObject.getAddressLine1());
		model.setAddressLine2(tObject.getAddressLine2());
		model.setCity(tObject.getCity());
		model.setState(tObject.getState());
		model.setZipCode(tObject.getZipCode());
		model.setPhotoUrl(tObject.getPhotoUrl());
		model.setSchoolId(tObject.getSchoolId());
		model.setEnabled(tObject.isEnabled());

		model.setCoach((tObject.getCoachId() == null) ? null : personService
				.get(tObject.getCoachId()));

		return model;
	}

}