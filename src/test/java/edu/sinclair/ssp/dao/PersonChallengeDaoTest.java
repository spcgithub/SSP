package edu.sinclair.ssp.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import edu.sinclair.ssp.model.ObjectStatus;
import edu.sinclair.ssp.model.Person;
import edu.sinclair.ssp.model.PersonChallenge;
import edu.sinclair.ssp.model.reference.Challenge;
import edu.sinclair.ssp.service.ObjectNotFoundException;
import edu.sinclair.ssp.service.PersonService;
import edu.sinclair.ssp.service.impl.SecurityServiceInTestEnvironment;
import edu.sinclair.ssp.service.reference.ChallengeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("reference/dao-testConfig.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class PersonChallengeDaoTest {

	// private static final Logger logger =
	// LoggerFactory.getLogger(PersonChallengeDaoTest.class);

	@Autowired
	private PersonChallengeDao dao;

	@Autowired
	private ChallengeService challengeService;

	@Autowired
	private PersonService personService;

	@Autowired
	private SecurityServiceInTestEnvironment securityService;

	private Challenge testChallenge;

	@Before
	public void setup() {
		securityService.setCurrent(new Person(Person.SYSTEM_ADMINISTRATOR_ID));
		testChallenge = challengeService.getAll(ObjectStatus.ACTIVE).get(0);
	}

	@Test
	public void testGet() throws ObjectNotFoundException {
		// test student = ken thompson
		Person person = personService.get(UUID
				.fromString("f549ecab-5110-4cc1-b2bb-369cac854dea"));

		List<PersonChallenge> modelsBefore = dao.forPerson(person);

		// save a new challenge for a person
		PersonChallenge model = new PersonChallenge(person, testChallenge);
		dao.save(model);

		List<PersonChallenge> modelsAfter = dao.forPerson(person);

		// we should see more than before
		assertTrue(modelsBefore.size() < modelsAfter.size());

		// makes sure the person is the same
		assertEquals(model.getPerson().getId(), person.getId());

		// fetch the saved one from the db
		PersonChallenge byId = dao.get(model.getId());
		assertEquals(byId.getId(), model.getId());

		PersonChallenge found = null;
		for (PersonChallenge pc : person.getChallenges()) {
			if (pc.getId().equals(model.getId())) {
				found = pc;
				break;
			}
		}
		assertNotNull(found);

		// delete it
		person.getChallenges().remove(found);
		dao.delete(model);

		assertNull(dao.get(model.getId()));

	}

	@Test
	public void testNull() {
		UUID id = UUID.randomUUID();
		PersonChallenge model = dao.get(id);
		assertNull(model);

		List<PersonChallenge> modelsAfter = dao.forPerson(new Person(id));
		assertEquals(0, modelsAfter.size());
	}

	@Test
	public void testGetAll() {
		dao.getAll(ObjectStatus.ALL);
		assertTrue(true);
	}

}