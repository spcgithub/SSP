package org.jasig.ssp.dao; // NOPMD by jon.adams on 5/17/12 8:23 PM

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jasig.ssp.dao.reference.ChallengeDao;
import org.jasig.ssp.dao.reference.ChallengeReferralDao;
import org.jasig.ssp.dao.reference.ConfidentialityLevelDao;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.Task;
import org.jasig.ssp.model.reference.Challenge;
import org.jasig.ssp.model.reference.ChallengeReferral;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.impl.SecurityServiceInTestEnvironment;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.junit.After;
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

import com.google.common.collect.Lists;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("reference/dao-testConfig.xml")
@TransactionConfiguration(defaultRollback = false)
@Transactional
public class TaskDaoTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TaskDaoTest.class);

	private static final UUID CONFIDENTIALITYLEVEL_ID = UUID
			.fromString("afe3e3e6-87fa-11e1-91b2-0026b9e7ff4c");

	private static final String CONFIDENTIALITYLEVEL_NAME = "EVERYONE";

	@Autowired
	private transient TaskDao dao;

	@Autowired
	private transient ConfidentialityLevelDao confidentialityLevelDao;

	@Autowired
	protected transient PersonService personService;

	@Autowired
	protected transient SecurityServiceInTestEnvironment securityService;

	@Autowired
	private transient ChallengeDao challengeDao;

	@Autowired
	private transient ChallengeReferralDao challengeReferralDao;

	protected transient Person ken;

	private transient ChallengeReferral testChallengeReferral;

	private transient Task testTask;

	@Before
	public void setUp() throws ObjectNotFoundException {
		try {
			ken = personService.personFromUsername("ken");
		} catch (final ObjectNotFoundException e) {
			LOGGER.error("can't find one of either sysadmin or ken");
		}
		securityService.setCurrent(ken);

		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 7);
		final Challenge testChallenge = challengeDao.get(UUID
				.fromString("af7e472c-3b7c-4d00-a667-04f52f560940"));
		testChallengeReferral = challengeReferralDao.get(UUID
				.fromString("19fbec43-8c0b-478b-9d5f-00ec6ec57511"));

		testTask = new Task("test task", cal.getTime(), ken, testChallenge,
				testChallengeReferral);
		dao.save(testTask);
	}

	@After
	public void destroy() {
		dao.delete(testTask);
	}

	/*
	 * public void crud() { This is in effect tested by the setup and teardown
	 * of the tests in this class }
	 */
	@Test
	public void getAllForPersonId() {
		assertList(dao.getAllForPersonId(ken.getId(), new SortingAndPaging(
				ObjectStatus.ACTIVE)).getRows());
	}

	@Test
	public void getAllForPersonIdComplete() {
		assertList(dao.getAllForPersonId(ken.getId(), true,
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForPersonIdIncomplete() {
		assertList(dao.getAllForPersonId(ken.getId(), false,
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForSessionId() {
		assertList(dao.getAllForSessionId("test session id",
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForSessionIdComplete() {
		assertList(dao.getAllForSessionId("test session id", true,
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForSessionIdIncomplete() {
		assertList(dao.getAllForSessionId("test session id", false,
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllWhichNeedRemindersSent() {
		assertList(dao.getAllWhichNeedRemindersSent(new SortingAndPaging(
				ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForPersonIdAndChallengeReferralId() {
		assertList(dao.getAllForPersonIdAndChallengeReferralId(ken.getId(),
				true, testChallengeReferral.getId(), new SortingAndPaging(
						ObjectStatus.ACTIVE)));
	}

	@Test
	public void getAllForSessionIdAndChallengeReferralId() {
		assertList(dao.getAllForSessionIdAndChallengeReferralId(
				"test sessionId", true, testChallengeReferral.getId(),
				new SortingAndPaging(ObjectStatus.ACTIVE)));
	}

	@Test
	public void getTasksInList() {
		final List<UUID> taskIds = Lists.newArrayList();
		taskIds.add(UUID.randomUUID());
		taskIds.add(UUID.randomUUID());
		taskIds.add(UUID.randomUUID());
		assertList(dao.getTasksInList(taskIds, new SortingAndPaging(
				ObjectStatus.ACTIVE)));
	}

	protected void assertList(final Collection<Task> objects) {
		for (final Task object : objects) {
			assertNotNull("List items should not be null.", object.getId());
		}
	}

	@Test
	public void testSaveNew() throws ObjectNotFoundException {
		UUID saved;

		Task obj = new Task();
		obj.setName("new name");
		obj.setDescription("new description");
		obj.setObjectStatus(ObjectStatus.ACTIVE);
		obj.setPerson(securityService.currentUser().getPerson());
		obj.setConfidentialityLevel(confidentialityLevelDao
				.load(CONFIDENTIALITYLEVEL_ID));
		dao.save(obj);

		assertNotNull("obj.id should not have been null.", obj.getId());
		saved = obj.getId();

		LOGGER.debug(obj.toString());

		obj = dao.get(saved);
		assertNotNull("Saved instance should not have been null.", obj);
		assertNotNull("Saved instance ID should not have been null.",
				obj.getId());
		assertEquals("Confidentiality level name did not match.",
				CONFIDENTIALITYLEVEL_NAME, obj.getConfidentialityLevel()
						.getName());

		final Collection<Task> all = dao.getAll(ObjectStatus.ACTIVE).getRows();
		assertFalse("GetAll() list should not have been empty.", all.isEmpty());
		assertList(all);

		dao.delete(obj);
	}
}