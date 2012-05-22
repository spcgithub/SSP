package org.jasig.ssp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.jasig.ssp.model.reference.FundingSource;

/**
 * Students may have zero or multiple Funding Sources.
 * 
 * The PersonFundingSource entity is an associative mapping between a student
 * (Person) and any Funding Sources they have.
 * 
 * Non-student users should never have any assigned Funding Sources.
 * 
 * @author jon.adams
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonFundingSource extends Auditable implements PersonAssoc,
		Serializable {

	private static final long serialVersionUID = -1349765434053823165L;

	@Column(length = 255)
	@Size(max = 255)
	private String description;

	/**
	 * Associated person. Changes to this Person are not persisted.
	 */
	@ManyToOne
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "person_id")
	private Person person;

	@ManyToOne
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "funding_source_id", nullable = false)
	private FundingSource fundingSource;

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public void setPerson(final Person person) {
		this.person = person;
	}

	public FundingSource getFundingSource() {
		return fundingSource;
	}

	public void setFundingSource(final FundingSource fundingSource) {
		this.fundingSource = fundingSource;
	}

	/**
	 * Overwrites simple properties with the parameter's properties.
	 * 
	 * @param source
	 *            Source to use for overwrites.
	 */
	public void overwrite(final PersonFundingSource source) {
		this.setDescription(source.getDescription());
	}

	@Override
	protected int hashPrime() {
		return 23;
	};

	@Override
	final public int hashCode() { // NOPMD by jon.adams on 5/9/12 7:16 PM
		int result = hashPrime();

		// Auditable properties
		result *= getId() == null ? "id".hashCode() : getId().hashCode();
		result *= getObjectStatus() == null ? hashPrime() : getObjectStatus()
				.hashCode();

		// PersonFundingSource
		result *= StringUtils.isEmpty(description) ? "description".hashCode()
				: description.hashCode();
		result *= (person == null) || (person.getId() == null) ? "person"
				.hashCode() : person.getId().hashCode();
		result *= fundingSource == null ? "fundingSource".hashCode()
				: fundingSource
						.hashCode();

		return result;
	}
}