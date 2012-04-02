package edu.sinclair.ssp.service.reference.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import edu.sinclair.ssp.dao.reference.SelfHelpGuideGroupDao;
import edu.sinclair.ssp.model.ObjectStatus;
import edu.sinclair.ssp.model.reference.SelfHelpGuideGroup;
import edu.sinclair.ssp.service.ObjectNotFoundException;

public class SelfHelpGuideGroupServiceTest {

	private SelfHelpGuideGroupServiceImpl service;
	private SelfHelpGuideGroupDao dao;

	@Before
	public void setup() {
		service = new SelfHelpGuideGroupServiceImpl();
		dao = createMock(SelfHelpGuideGroupDao.class);

		service.setDao(dao);
	}

	@Test
	public void testGetAll() {
		List<SelfHelpGuideGroup> daoAll = new ArrayList<SelfHelpGuideGroup>();
		daoAll.add(new SelfHelpGuideGroup());

		expect(dao.getAll(ObjectStatus.ACTIVE)).andReturn(daoAll);

		replay(dao);

		List<SelfHelpGuideGroup> all = service.getAll(ObjectStatus.ACTIVE);
		assertTrue(all.size() > 0);
		verify(dao);
	}

	@Test
	public void testGet() throws ObjectNotFoundException {
		UUID id = UUID.randomUUID();
		SelfHelpGuideGroup daoOne = new SelfHelpGuideGroup(id);

		expect(dao.get(id)).andReturn(daoOne);

		replay(dao);

		assertNotNull(service.get(id));
		verify(dao);
	}

	@Test
	public void testSave() throws ObjectNotFoundException {
		UUID id = UUID.randomUUID();
		SelfHelpGuideGroup daoOne = new SelfHelpGuideGroup(id);

		expect(dao.get(id)).andReturn(daoOne);
		expect(dao.save(daoOne)).andReturn(daoOne);

		replay(dao);

		assertNotNull(service.save(daoOne));
		verify(dao);
	}

	@Test
	public void testDelete() throws ObjectNotFoundException {
		UUID id = UUID.randomUUID();
		SelfHelpGuideGroup daoOne = new SelfHelpGuideGroup(id);

		expect(dao.get(id)).andReturn(daoOne).times(2);
		expect(dao.save(daoOne)).andReturn(daoOne);
		expect(dao.get(id)).andReturn(null);

		replay(dao);

		service.delete(id);

		boolean found = true;
		try {
			service.get(id);
		} catch (ObjectNotFoundException e) {
			found = false;
		}

		assertFalse(found);
		verify(dao);
	}

}