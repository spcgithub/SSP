package org.jasig.ssp.web.api;

import java.util.UUID;

import org.jasig.ssp.model.Auditable;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.transferobject.PagingTO;
import org.jasig.ssp.transferobject.ServiceResponse;
import org.jasig.ssp.transferobject.TransferObject;

/**
 * All the Methods a Reference Controller needs to be useful.
 * 
 * @param <TO>
 *            The TO type this controller works with.
 * @param <T>
 *            The model that the TO type T works with.
 */
public abstract class RestController<TO extends TransferObject<T>, T extends Auditable>
		extends
		BaseController {

	/**
	 * Retrieve every instance in the database filtered by the supplied status.
	 * 
	 * @param status
	 *            Filter by this status.
	 * @param start
	 *            First result (0-based index) to return. Parameter must be a
	 *            positive, non-zero integer. Often comes from client as a
	 *            parameter labeled <code>start</code>. A null value indicates
	 *            to return rows starting from index 0.
	 * @param limit
	 *            Maximum number of results to return. Parameter must be a
	 *            positive, non-zero integer. Often comes from client as a
	 *            parameter labeled <code>limit</code>. A null value indicates
	 *            return all rows from the start parameter to the end of the
	 *            data.
	 * @param sort
	 *            Property name. If null or empty string, the default sort will
	 *            be used. If non-empty, must be a case-sensitive model property
	 *            name. Often comes from client as a parameter labeled
	 *            <code>sort</code>. Example sort expression:
	 *            <code>propertyName</code>
	 * @param sortDirection
	 *            Ascending/descending keyword. If null or empty string, the
	 *            default sort will be used. Must be <code>ASC</code> or
	 *            <code>DESC</code>.
	 * @exception Exception
	 *                Generic exception thrown if there were any errors.
	 * @return All entities in the database filtered by the supplied status.
	 */
	public abstract PagingTO<TO, T> getAll(ObjectStatus status, Integer start,
			Integer limit, String sort, String sortDirection) throws Exception;

	/**
	 * Retrieves the specified instance from persistent storage.
	 * 
	 * @param id
	 *            The specific id to use to lookup the associated data.
	 * @return The specified instance if found.
	 * @throws Exception
	 *             If there were any unexpected exceptions thrown.
	 */
	public abstract TO get(UUID id) throws Exception;

	/**
	 * Persist a new instance of the specified object.
	 * <p>
	 * Must not include an id.
	 * 
	 * @param obj
	 *            New instance to persist.
	 * @return Original instance plus the generated id.
	 * @throws org.jasig.ssp.web.api.validation.ValidationException
	 *             If the obj contains an id (since it shouldn't).
	 * @throws Exception
	 *             If there were any unexpected exceptions thrown.
	 */
	public abstract TO create(TO obj) throws Exception;

	/**
	 * Persist any changes to the specified instance.
	 * 
	 * @param id
	 *            Explicit id to the instance to persist.
	 * @param obj
	 *            Full instance to persist.
	 * @return The update data object instance.
	 * @throws org.jasig.ssp.web.api.validation.ValidationException
	 *             If the specified id is null.
	 * @throws Exception
	 *             If there were any unexpected exceptions thrown.
	 */
	public abstract TO save(UUID id, TO obj) throws Exception;

	/**
	 * Marks the specified data instance with a status of
	 * {@link ObjectStatus#DELETED}.
	 * 
	 * @param id
	 *            The id of the data instance to mark deleted.
	 * @return Success boolean.
	 * @throws Exception
	 *             If there were any unexpected exceptions thrown.
	 */
	public abstract ServiceResponse delete(UUID id) throws Exception;
}