package edu.sinclair.ssp.service.reference;

import java.util.List;
import java.util.UUID;

import edu.sinclair.ssp.model.ObjectStatus;
import edu.sinclair.ssp.model.reference.ConfidentialityDisclosureAgreement;
import edu.sinclair.ssp.service.AuditableCrudService;
import edu.sinclair.ssp.service.ObjectNotFoundException;

public interface ConfidentialityDisclosureAgreementService extends
		AuditableCrudService<ConfidentialityDisclosureAgreement> {

	/**
	 * Retrieve every instance in the database filtered by the supplied status.
	 * 
	 * @param status
	 *            Filter by this status.
	 * @return All entities in the database filtered by the supplied status.
	 */
	@Override
	public List<ConfidentialityDisclosureAgreement> getAll(ObjectStatus status);

	/**
	 * Retrieve every instance in the database filtered by the supplied status.
	 * 
	 * @param status
	 *            Filter by this status.
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
	 * @return All entities in the database filtered by the supplied status.
	 */
	@Override
	public List<ConfidentialityDisclosureAgreement> getAll(ObjectStatus status, int firstResult,
			int maxResults, String sortExpression);

	@Override
	public ConfidentialityDisclosureAgreement get(UUID id) throws ObjectNotFoundException;

	@Override
	public ConfidentialityDisclosureAgreement create(ConfidentialityDisclosureAgreement obj);

	@Override
	public ConfidentialityDisclosureAgreement save(ConfidentialityDisclosureAgreement obj)
			throws ObjectNotFoundException;

	/**
	 * Mark the specific instance as {@link ObjectStatus#DELETED}.
	 * 
	 * @param id
	 *            Instance identifier
	 * @exception ObjectNotFoundException
	 *                if the specified ID does not exist.
	 */
	@Override
	public void delete(UUID id) throws ObjectNotFoundException;

}