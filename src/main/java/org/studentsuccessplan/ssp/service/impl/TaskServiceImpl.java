package org.studentsuccessplan.ssp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studentsuccessplan.ssp.dao.TaskDao;
import org.studentsuccessplan.ssp.model.Task;
import org.studentsuccessplan.ssp.service.ObjectNotFoundException;
import org.studentsuccessplan.ssp.service.TaskService;

@Service
public class TaskServiceImpl
		extends AbstractAbstractTaskService<Task>
		implements TaskService {

	@Autowired
	private TaskDao dao;

	@Override
	protected TaskDao getDao() {
		return dao;
	}

	@Override
	public Task save(Task obj) throws ObjectNotFoundException {
		Task current = getDao().get(obj.getId());

		current.setChallenge(obj.getChallenge());
		current.setChallengeReferral(obj.getChallengeReferral());

		current.setCompletedDate(obj.getCompletedDate());
		current.setDescription(obj.getDescription());
		current.setDueDate(obj.getDueDate());
		current.setObjectStatus(obj.getObjectStatus());
		current.setPerson(obj.getPerson());
		current.setReminderSentDate(obj.getReminderSentDate());
		current.setSessionId(obj.getSessionId());

		return getDao().save(current);

	}
}