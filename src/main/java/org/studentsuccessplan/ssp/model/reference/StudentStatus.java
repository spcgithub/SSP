package org.studentsuccessplan.ssp.model.reference;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * StudentStatus reference object.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StudentStatus extends AbstractReference implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public StudentStatus() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identifier; required
	 */

	public StudentStatus(UUID id) {
		super(id);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identifier; required
	 * @param name
	 *            Name; required; max 100 characters
	 */

	public StudentStatus(UUID id, String name) {
		super(id, name);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identifier; required
	 * @param name
	 *            Name; required; max 100 characters
	 * @param description
	 *            Description; max 150 characters
	 */
	public StudentStatus(UUID id, String name, String description) {
		super(id, name, description);
	}
}