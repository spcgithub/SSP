package org.studentsuccessplan.ssp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

import org.studentsuccessplan.ssp.security.SspUser;
import org.studentsuccessplan.ssp.service.ObjectNotFoundException;
import org.studentsuccessplan.ssp.service.PersonService;
import org.studentsuccessplan.ssp.service.SecurityService;

@Transactional(readOnly = true)
public class SecurityServiceImpl implements SecurityService {

	private Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);

	@Autowired
	private PersonService personService;

	@Override
	public SspUser currentlyLoggedInSspUser() {
		SspUser sspUser = null;
		if (SecurityContextHolder.getContext().getAuthentication()
				.isAuthenticated()) {
			Object principal = SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			if (principal instanceof SspUser) {
				sspUser = (SspUser) principal;
			} else if (principal instanceof String) {
				logger.error("Just tried to get an sspUser object from a user that is "
						+ principal);
				return null;
			} else {
				logger.error("Just tried to get an sspUser object from an object that is really a "
						+ principal.toString());
				return null;
			}
		} else {
			return null;
		}

		if (sspUser.getPerson() == null) {
			try {
				sspUser.setPerson(personService.personFromUsername(sspUser
						.getUsername()));
			} catch (ObjectNotFoundException e) {
				logger.error("Did not find the person's domain object");
			}
		}

		return sspUser;
	}

	@Override
	public boolean isAuthenticated() {
		return SecurityContextHolder.getContext().getAuthentication()
				.isAuthenticated();
	}

	@Override
	public String getSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}
}