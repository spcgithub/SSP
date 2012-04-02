package edu.sinclair.ssp.dao.reference;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import edu.sinclair.ssp.dao.AbstractAuditableCrudDao;
import edu.sinclair.ssp.model.Auditable;
import edu.sinclair.ssp.model.ObjectStatus;

/**
 * Base CRUD methods for reference model objects.
 * <p>
 * Defaults to sorting by the <code>Name</code> property unless otherwise
 * specified.
 * 
 * @param <T>
 *            Any domain type that extends the Auditable class.
 */
@Repository
public abstract class ReferenceAuditableCrudDao<T extends Auditable> extends
		AbstractAuditableCrudDao<T> {

	protected ReferenceAuditableCrudDao(Class<T> persistentClass) {
		super(persistentClass);
	}

	/**
	 * Return all entities in the database, filtered only by the specified
	 * parameters.
	 * 
	 * @param status
	 *            Object status filter. Set to {@link ObjectStatus#ALL} to
	 *            return all results.
	 * @return All entities in the database, filtered only by the specified
	 *         parameters.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll(ObjectStatus status) {
		Criteria query = super.sessionFactory.getCurrentSession()
				.createCriteria(super.persistentClass);

		if (status != ObjectStatus.ALL) {
			query.add(Restrictions.eq("objectStatus", status));
		}

		query.addOrder(Order.asc("name"));

		return query.list();
	}

	/**
	 * Return all entities in the database, filtered only by the specified
	 * parameters.
	 * 
	 * @param status
	 *            Object status filter. Set to {@link ObjectStatus#ALL} to
	 *            return all results.
	 * @param firstResult
	 *            First result (0-based index) to return. Parameter must be a
	 *            positive, non-zero integer.
	 * @param maxResults
	 *            Maximum number of results to return. Parameter must be a
	 *            positive, non-zero integer.
	 * @param sortExpression
	 *            Property name and ascending/descending keyword. If null or
	 *            empty string, the default sort order will be used. Example
	 *            sort expression: <code>propertyName ASC</code>
	 * @return All entities in the database, filtered only by the specified
	 *         parameters.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> getAll(ObjectStatus status, int firstResult, int maxResults,
			String sortExpression) {
		if (firstResult < 0) {
			throw new IllegalArgumentException(
					"firstResult must be 0 or greater.");
		}

		if (maxResults < 1) {
			throw new IllegalArgumentException(
					"maxResults must be 1 or greater.");
		}

		Criteria query = sessionFactory.getCurrentSession()
				.createCriteria(persistentClass).setFirstResult(firstResult)
				.setMaxResults(maxResults);

		if (StringUtils.isEmpty(sortExpression)) {
			query.addOrder(Order.asc("name"));
		} else if (StringUtils.endsWithIgnoreCase(sortExpression, "asc")) {
			query.addOrder(Order.asc(StringUtils.substringBefore(
					sortExpression, " ")));
		} else if (StringUtils.endsWithIgnoreCase(sortExpression, "desc")) {
			query.addOrder(Order.desc(StringUtils.substringBefore(
					sortExpression, " ")));
		} else {
			throw new IllegalArgumentException(
					"Invalid sort expression. Must be in the form of \"propertyName ASC\" or \"propertyName DESC\".");
		}

		if (status != ObjectStatus.ALL) {
			query.add(Restrictions.eq("objectStatus", status));
		}

		return query.list();
	}
}