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
package org.jasig.ssp.web.api; // NOPMD

import static org.jasig.ssp.util.assertions.SspAssert.assertNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.hibernate.SessionFactory;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonSearchService;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.impl.SecurityServiceInTestEnvironment;
import org.jasig.ssp.transferobject.PagedResponse;
import org.jasig.ssp.transferobject.PersonSearchResultTO;
import org.jasig.ssp.web.api.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link PersonSearchController} tests
 * 
 * @author jon.adams
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("../ControllerIntegrationTests-context.xml")
@TransactionConfiguration()
@Transactional
public class PersonSearchControllerIntegrationTest {

	@Autowired
	private transient PersonSearchController controller;

	@Autowired
	protected transient SessionFactory sessionFactory;

	@Autowired
	protected transient PersonSearchService personSearchService;

	@Autowired
	protected transient PersonService personService;

	private static final String PERSON_LAST_NAME = "Gosling";

	@Autowired
	private transient SecurityServiceInTestEnvironment securityService;

	/**
	 * Setup the security service with the administrator user.
	 */
	@Before
	public void setUp() {
		securityService.setCurrent(new Person(Person.SYSTEM_ADMINISTRATOR_ID),
				"ROLE_PERSON_CHALLENGE_READ",
				"ROLE_PERSON_CHALLENGE_WRITE",
				"ROLE_PERSON_CHALLENGE_DELETE",
				"ROLE_PERSON_READ");
	}

	/**
	 * Test the
	 * {@link PersonSearchController#search(String, UUID, Boolean, Boolean, ObjectStatus, Integer, Integer, String, String)}
	 * action.
	 * 
	 * @throws ObjectNotFoundException
	 *             If object could not be found.
	 */
	@Test
	public void testControllerSearch() throws ObjectNotFoundException, ValidationException {
		final PagedResponse<PersonSearchResultTO> results = controller.search(
				PERSON_LAST_NAME, null, null, Boolean.TRUE, ObjectStatus.ACTIVE, 0,
				10, null, null, new MockHttpServletRequest());

		assertNotNull("Results list should not have been null.", results);
		assertNotEmpty("Results list should not have been empty.",
				results.getRows());

		final PersonSearchResultTO result = results.getRows().iterator().next();
		assertNotNull("Gosling should have had a coach.", result.getCoach()
				.getId());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testControllerCreateWithInvalidData()
			throws ValidationException, ObjectNotFoundException {
		controller.search(PERSON_LAST_NAME, UUID.randomUUID(), null, Boolean.FALSE,
				ObjectStatus.ACTIVE, 0, 10, null, null, new MockHttpServletRequest());
		fail("Create with invalid ProgramStatus UUID should have thrown exception.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testControllerSearchWithNullTerm()
			throws ObjectNotFoundException, ValidationException {
		controller.search(null, null, null, Boolean.TRUE, ObjectStatus.ACTIVE, 0, 10,
				null, null, new MockHttpServletRequest());
		fail("Invalid search should have thrown exception.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testControllerSearchWithEmptyTerm()
			throws ObjectNotFoundException, ValidationException {
		controller.search(" ", null, null, Boolean.TRUE, ObjectStatus.ACTIVE, 0, 10,
				null, null, new MockHttpServletRequest());
		fail("Invalid search should have thrown exception.");
	}

	/**
	 * Test that getLogger() returns the matching log class name for the current
	 * class under test.
	 */
	@Test
	public void testLogger() {
		final Logger logger = controller.getLogger();
		assertEquals("Log class name did not match.", controller.getClass()
				.getName(), logger.getName());
	}
}