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

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jasig.ssp.model.RestrictedPersonAssocAuditable;
import org.jasig.ssp.model.reference.ConfidentialityLevel;
import org.jasig.ssp.security.SspUser;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.reference.ConfidentialityLevelService;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRestrictedPersonAssocAuditableCrudDao<T extends RestrictedPersonAssocAuditable>
		extends AbstractPersonAssocAuditableCrudDao<T>
		implements RestrictedPersonAssocAuditableDao<T> {

	protected AbstractRestrictedPersonAssocAuditableCrudDao(
			final Class<T> persistentClass) {
		super(persistentClass);
	}

	protected static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractRestrictedPersonAssocAuditableCrudDao.class);

	@Autowired
	private transient ConfidentialityLevelService confidentialityLevelService;

	protected void addConfidentialityLevelsRestriction(
			final SspUser requestor,
			final Criteria criteria) {

		final Collection<ConfidentialityLevel> levels = confidentialityLevelService
				.filterConfidentialityLevelsFromGrantedAuthorities(requestor
						.getAuthorities());

		if (levels.isEmpty()) {
			try {
				levels.add(confidentialityLevelService.get(ConfidentialityLevel.CONFIDENTIALITYLEVEL_EVERYONE));
			} catch (ObjectNotFoundException e) {
				LOGGER.error(e.getLocalizedMessage());
			}
		}
		criteria.add(Restrictions.or(
				Restrictions.in("confidentialityLevel", levels),
				Restrictions.eq("createdBy.id", requestor.getPerson()
						.getId())));
		LOGGER.debug("Number of Confidentiality Levels for user {}",
				levels.size());
	}

	@Override
	public PagingWrapper<T> getAllForPersonId(final UUID personId,
			final SspUser requestor,
			final SortingAndPaging sAndP) {

		final Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq("person.id", personId));

		addConfidentialityLevelsRestriction(requestor, criteria);

		return processCriteriaWithStatusSortingAndPaging(criteria, sAndP);
	}

	/**
	 * this method will throw UnsupportedOperationException. Use
	 * getAllForPersonId with personId, requester and sAndP instead.
	 */
	@Override
	public PagingWrapper<T> getAllForPersonId(final UUID personId,
			final SortingAndPaging sAndP) {
		throw new UnsupportedOperationException(
				"For Restricted Person Associated Auditables, you must call getAllForPersonId and supply a requestor");
	}

	@Override
	@SuppressWarnings(UNCHECKED)
	public List<T> get(@NotNull final List<UUID> ids,
			@NotNull final SspUser requester, final SortingAndPaging sAndP) {
		if (ids == null || ids.isEmpty()) {
			throw new IllegalArgumentException(
					"List of ids can not be null or empty.");
		}

		if (requester == null) {
			throw new IllegalArgumentException(
					"Requester can not be null.");
		}

		final Criteria criteria = createCriteria(sAndP);
		criteria.add(Restrictions.in("id", ids));

		addConfidentialityLevelsRestriction(requester, criteria);

		return criteria.list();
	}
}