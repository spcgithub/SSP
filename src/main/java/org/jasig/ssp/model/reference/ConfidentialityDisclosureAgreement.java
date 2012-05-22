package org.jasig.ssp.model.reference;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

/**
 * ConfidentialityDisclosureAgreement reference object.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ConfidentialityDisclosureAgreement extends AbstractReference
		implements Serializable {

	private static final long serialVersionUID = -2992853251827812441L;

	/**
	 * text of the agreement
	 * 
	 * Optional, null allowed, max length 64000 characters.
	 */
	@Column(nullable = false, length = 64000)
	@Size(max = 64000)
	private String text;

	/**
	 * Constructor
	 */
	public ConfidentialityDisclosureAgreement() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            Identifier; required
	 */

	public ConfidentialityDisclosureAgreement(final UUID id) {
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

	public ConfidentialityDisclosureAgreement(final UUID id, final String name) {
		super(id, name);
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	@Override
	protected int hashPrime() {
		return 73;
	};

	@Override
	public int hashCode() {
		int result = hashPrime() * super.hashCode();

		result *= StringUtils.isEmpty(text) ? "text".hashCode() : text
				.hashCode();

		return result;
	}
}