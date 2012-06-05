package org.jasig.ssp.web.api.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.impl.SecurityServiceInTestEnvironment;
import org.jasig.ssp.transferobject.reference.MessageTemplateTO;
import org.jasig.ssp.web.api.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link MessageTemplateController} tests
 * 
 * @author jon.adams
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("../../ControllerIntegrationTests-context.xml")
@TransactionConfiguration
@Transactional
public class MessageTemplateControllerIntegrationTest {

	@Autowired
	private transient MessageTemplateController controller;

	private static final UUID MESSAGETEMPLATE_ID = UUID
			.fromString("7c945020-86b0-11e1-849a-0026b9e7ff4c");

	private static final String MESSAGETEMPLATE_NAME = "empty_template";

	@Autowired
	private transient SecurityServiceInTestEnvironment securityService;

	/**
	 * Setup the security service with the administrator user for use by
	 * {@link #testControllerCreateAndDelete()} that checks that the Auditable
	 * auto-fill properties are correctly filled.
	 */
	@Before
	public void setUp() {
		securityService.setCurrent(new Person(Person.SYSTEM_ADMINISTRATOR_ID));
	}

	/**
	 * Test the {@link MessageTemplateController#get(UUID)} action.
	 * 
	 * @throws Exception
	 *             Thrown if the controller throws any exceptions.
	 */
	@Test
	public void testControllerGet() throws Exception {
		assertNotNull(
				"Controller under test was not initialized by the container correctly.",
				controller);

		final MessageTemplateTO obj = controller.get(MESSAGETEMPLATE_ID);

		assertNotNull(
				"Returned MessageTemplateTO from the controller should not have been null.",
				obj);

		assertEquals("Returned MessageTemplate.Name did not match.",
				MESSAGETEMPLATE_NAME, obj.getName());
	}

	/**
	 * Test that the {@link MessageTemplateController#get(UUID)} action returns
	 * the correct validation errors when an invalid ID is sent.
	 * 
	 * @throws Exception
	 *             Thrown if the controller throws any exceptions.
	 */
	@Test(expected = ObjectNotFoundException.class)
	public void testControllerGetOfInvalidId() throws Exception {
		assertNotNull(
				"Controller under test was not initialized by the container correctly.",
				controller);

		final MessageTemplateTO obj = controller.get(UUID.randomUUID());

		assertNull(
				"Returned MessageTemplateTO from the controller should have been null.",
				obj);
	}

	/**
	 * Test the {@link MessageTemplateController#create(MessageTemplateTO)} and
	 * {@link MessageTemplateController#delete(UUID)} actions.
	 * 
	 * @throws Exception
	 *             Thrown if the controller throws any exceptions.
	 */
	@Test
	public void testControllerCreateAndDelete() throws Exception {
		assertNotNull(
				"Controller under test was not initialized by the container correctly.",
				controller);

		final String testString1 = "testString1";
		final String testString2 = "testString1";

		// Check validation of 'no ID for create()'
		try {
			final MessageTemplateTO obj = controller
					.create(new MessageTemplateTO(
							UUID
									.randomUUID(),
							testString1, testString2)); // NOPMD by
														// jon.adams
			assertNull(
					"Calling create with an object with an ID should have thrown a validation excpetion.",
					obj);
		} catch (ValidationException exc) {
			assertNotNull("ValidatedException was expected to be thrown.", exc);
		}

		// Now create a valid MessageTemplate
		final MessageTemplateTO valid = controller
				.create(new MessageTemplateTO(
						null,
						testString1,
						testString2)); // NOPMD

		assertNotNull(
				"Returned MessageTemplateTO from the controller should not have been null.",
				valid);
		assertNotNull(
				"Returned MessageTemplateTO.ID from the controller should not have been null.",
				valid.getId());
		assertEquals(
				"Returned MessageTemplateTO.Name from the controller did not match.",
				testString1, valid.getName());
		assertEquals(
				"Returned MessageTemplateTO.CreatedBy was not correctly auto-filled for the current user (the administrator in this test suite).",
				Person.SYSTEM_ADMINISTRATOR_ID, valid.getCreatedBy().getId());

		assertTrue("Delete action did not return success.",
				controller.delete(valid.getId()).isSuccess());
	}

	/**
	 * Test the
	 * {@link MessageTemplateController#getAll(ObjectStatus, Integer, Integer, String, String)}
	 * action.
	 * 
	 * @throws Exception
	 *             Thrown if the controller throws any exceptions.
	 */
	@Test
	public void testControllerAll() throws Exception {
		final Collection<MessageTemplateTO> list = controller.getAll(
				ObjectStatus.ACTIVE, null, null, null, null).getRows();

		assertNotNull("List should not have been null.", list);
		assertFalse("List action should have returned some objects.",
				list.isEmpty());
	}

}