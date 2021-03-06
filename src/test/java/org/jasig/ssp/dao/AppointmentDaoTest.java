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
package org.jasig.ssp.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jasig.ssp.TestUtils;
import org.jasig.ssp.model.Appointment;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.impl.SecurityServiceInTestEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("dao-testConfig.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class AppointmentDaoTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AppointmentDaoTest.class);

	@Autowired
	private transient AppointmentDao dao;

	@Autowired
	private transient SecurityServiceInTestEnvironment securityService;

	@Autowired
	private transient SessionFactory sessionFactory;

	@Autowired
	private transient PersonService personService;

	private Person james; // NOPMD

	@Before
	public void setUp() throws ObjectNotFoundException {
		james = personService.get(UUID
				.fromString("1010e4a0-1001-0110-1011-4ffc02fe81ff"));
		securityService.setCurrent(james);
	}

	@Test
	public void testSaveNew() throws ObjectNotFoundException {
		final Date startDate = new Date(1339419600000L); // "Mon JUN 11 09:00:00 EDT 2012"
		final Date endDate = new Date(1339423200000L); // "Mon JUN 11 09:00:00 EDT 2012"

		Appointment obj = new Appointment();
		obj.setEndTime(endDate);
		obj.setStartTime(startDate);
		obj.setPerson(securityService.currentUser().getPerson());

		obj = dao.save(obj);

		assertNotNull("Saved obj should not have been null", obj.getId());
		final UUID saved = obj.getId();

		// flush to storage, then clear out in-memory version
		final Session session = sessionFactory.getCurrentSession();
		session.flush();
		session.evict(obj);

		obj = dao.get(saved);

		LOGGER.debug("testSaveNew(): Saved " + obj.toString());
		assertNotNull("Reloaded object should not have been null.", obj);
		assertNotNull("Reloaded ID should not have been null.", obj.getId());

		final List<Appointment> all = (List<Appointment>) dao.getAll(
				ObjectStatus.ACTIVE)
				.getRows();
		assertNotNull("GetAll list should not have been null.", all);
		assertFalse("GetAll list should not have been empty.", all.isEmpty());
		TestUtils.assertListDoesNotContainNullItems(all);

		dao.delete(obj);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testNull() throws ObjectNotFoundException {
		dao.get(UUID.randomUUID());
		fail("Result of invalid get() should have thrown an exception.");
	}

	@Test
	public void getCurrentAppointmentForPerson() {
		final Appointment appt = dao
				.getCurrentAppointmentForPerson(securityService
						.currentUser().getPerson());
		assertNotNull("Appointment should not have been null.", appt);
	}
}