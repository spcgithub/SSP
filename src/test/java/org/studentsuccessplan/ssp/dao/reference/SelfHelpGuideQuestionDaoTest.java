package org.studentsuccessplan.ssp.dao.reference;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

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

import org.studentsuccessplan.ssp.model.ObjectStatus;
import org.studentsuccessplan.ssp.model.Person;
import org.studentsuccessplan.ssp.model.reference.Challenge;
import org.studentsuccessplan.ssp.model.reference.SelfHelpGuide;
import org.studentsuccessplan.ssp.model.reference.SelfHelpGuideQuestion;
import org.studentsuccessplan.ssp.service.impl.SecurityServiceInTestEnvironment;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("dao-testConfig.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class SelfHelpGuideQuestionDaoTest {

	private static final Logger logger = LoggerFactory
			.getLogger(SelfHelpGuideQuestionDaoTest.class);

	@Autowired
	private SelfHelpGuideQuestionDao dao;

	@Autowired
	private SelfHelpGuideDao selfHelpGuideDao;

	@Autowired
	private ChallengeDao challengeDao;

	@Autowired
	private SecurityServiceInTestEnvironment securityService;

	private SelfHelpGuide testGuide;
	private Challenge testChallenge;

	@Before
	public void setup() {
		securityService.setCurrent(new Person(Person.SYSTEM_ADMINISTRATOR_ID));
		testGuide = selfHelpGuideDao.get(UUID
				.fromString("2597d6a8-c95e-40a5-a3fd-e0d95967b1a0"));
		testChallenge = challengeDao.getAll(ObjectStatus.ACTIVE).get(0);
	}

	@Test
	public void testSaveNew() {
		UUID saved;

		SelfHelpGuideQuestion obj = new SelfHelpGuideQuestion();
		obj.setName("new name");
		obj.setObjectStatus(ObjectStatus.ACTIVE);
		obj.setChallenge(testChallenge);
		obj.setSelfHelpGuide(testGuide);
		dao.save(obj);

		assertNotNull(obj.getId());
		saved = obj.getId();

		logger.debug(obj.toString());

		obj = dao.get(saved);
		assertNotNull(obj);
		assertNotNull(obj.getId());
		assertNotNull(obj.getName());

		List<SelfHelpGuideQuestion> all = dao.getAll(ObjectStatus.ACTIVE);
		assertNotNull(all);
		assertTrue(all.size() > 0);
		assertList(all);

		dao.delete(obj);
	}

	@Test
	public void testNull() {
		UUID id = UUID.randomUUID();
		SelfHelpGuideQuestion selfHelpGuideQuestion = dao.get(id);

		assertNull(selfHelpGuideQuestion);
	}

	private void assertList(List<SelfHelpGuideQuestion> objects) {
		for (SelfHelpGuideQuestion object : objects) {
			assertNotNull(object.getId());
		}
		assertTrue(true);
	}

	@Test
	public void uuidGeneration() {
		SelfHelpGuideQuestion obj = new SelfHelpGuideQuestion();
		obj.setName("new name");
		obj.setObjectStatus(ObjectStatus.ACTIVE);
		obj.setChallenge(testChallenge);
		obj.setSelfHelpGuide(testGuide);
		dao.save(obj);

		SelfHelpGuideQuestion obj2 = new SelfHelpGuideQuestion();
		obj2.setName("new name");
		obj2.setObjectStatus(ObjectStatus.ACTIVE);
		obj2.setChallenge(testChallenge);
		obj2.setSelfHelpGuide(testGuide);
		dao.save(obj2);

		logger.debug("obj1 id: " + obj.getId().toString() + ", obj2 id: "
				+ obj2.getId().toString());

		dao.delete(obj);
		dao.delete(obj2);
	}

	@Test
	public void bySelfHelpGuide() {
		assertList(dao.bySelfHelpGuide(UUID.randomUUID()));
	}

}