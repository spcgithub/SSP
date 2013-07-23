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
package org.jasig.ssp.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.SessionFactory;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.security.SspUser;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;

@Transactional(readOnly = true)
public class SecurityServiceImpl implements SecurityService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SecurityServiceImpl.class);

	@Autowired
	protected transient SessionFactory sessionFactory;

	@Autowired
	private transient PersonService personService;

	@Override
	public SspUser anonymousUser() {

		LOGGER.debug("Using the Anonymous User");

		final SspUser user = new SspUser(SspUser.ANONYMOUS_PERSON_USERNAME,
				"", true, true, true, true,
				new ArrayList<GrantedAuthority>(0));

		user.setPerson(personService.load(SspUser.ANONYMOUS_PERSON_ID));

		return user;
	}

	@Override
	public SspUser noAuthAdminUser() {
		LOGGER.debug("Using the No Authentication Admin User");

		final SspUser user = new SspUser("no auth admin user", "", false,
				false, false, false, new ArrayList<GrantedAuthority>(0));

		user.setPerson(personService.load(Person.SYSTEM_ADMINISTRATOR_ID));

		return user;
	}

	@Override
	public SspUser currentUser() {
		SspUser sspUser = null;

		// assumption: SecurityContext only returns null for Authentication when
		// there is no actual web context

		final Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();

		if (null == auth) {
			// not authenticated, return null
			return null;
		}

		if (auth.isAuthenticated()) {
			final Object principal = auth.getPrincipal();

			if (principal instanceof SspUser) {
				sspUser = (SspUser) principal;

			} else if (principal instanceof String) {

				if (SspUser.ANONYMOUS_PERSON_USERNAME.equals(principal)) {
					sspUser = anonymousUser();
				} else {
					LOGGER.error("Just tried to get an sspUser object from a user that is "
							+ principal);
					// authenticated, but something is wrong with the principal
					return null;
				}

			} else {
				// authenticated, but something is wrong with the principal
				LOGGER.error("Just tried to get an sspUser object from an object that is really a "
						+ principal.toString());
				return null;
			}
		} else {
			// not authenticated, return null
			return null;
		}

		if (sspUser.getPerson() == null) {
			try {
				sspUser.setPerson(personService.personFromUsername(sspUser
						.getUsername()));
			} catch (ObjectNotFoundException e) {
				LOGGER.error("Did not find the person's domain object");
				return null;
			}
		}

		return sspUser;
	}

	@Override
	public SspUser currentFallingBackToAdmin() {
		final SspUser user = currentUser();

		if (null == user) {
			return noAuthAdminUser();
		} else {
			return user;
		}
	}

	@Override
	public SspUser currentlyAuthenticatedUser() {
		final SspUser sspUser = currentUser();

		if (sspUser == null) {
			LOGGER.trace("User is not authenticated");

		} else if (SspUser.ANONYMOUS_PERSON_USERNAME.equals(sspUser
				.getUsername())) {
			LOGGER.trace("Is anonymous user");
			return null;

		} else if (sspUser.getPerson() == null) {
			LOGGER.trace("User is not in the person table");
			return null;

		}

		return sspUser;
	}

	@Override
	public boolean isAuthenticated() {
		final boolean authenticated = (SecurityContextHolder.getContext()
				.getAuthentication() != null)
				&& SecurityContextHolder.getContext().getAuthentication()
						.isAuthenticated();

		final SspUser currentUser = currentUser();

		if (authenticated) {
			if ((currentUser == null)) {
				LOGGER.trace("User is authenticated, but not in the person table");
				return false;
			} else {
				LOGGER.trace("User is authenticated");
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean hasAuthority(final String authority) {

		final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder
				.getContext().getAuthentication().getAuthorities();

		for (GrantedAuthority auth : authorities) {
			if (auth.getAuthority().equalsIgnoreCase(authority)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}

	@Override
	public void afterRequest() {
		SspUser.afterRequest();
	}
}
