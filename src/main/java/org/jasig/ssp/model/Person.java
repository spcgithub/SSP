package org.jasig.ssp.model; // NOPMD

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;
import org.jasig.ssp.model.tool.PersonTool;

/**
 * A Person entity.
 * 
 * Usually represents either a user of the back-end system, or a student.
 * 
 * @author jon.adams
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public final class Person extends Auditable implements Serializable { // NOPMD

	private static final long serialVersionUID = 4122282021549627683L;

	/**
	 * Static, super administrator account identifier. Only used by IT and
	 * support staff, never by students or users of the system.
	 */
	public static final UUID SYSTEM_ADMINISTRATOR_ID = UUID
			.fromString("58ba5ee3-734e-4ae9-b9c5-943774b4de41");

	/**
	 * First name; required.
	 * 
	 * Maximum length of 50.
	 */
	@Column(nullable = false, length = 50)
	@NotNull
	@NotEmpty
	@Size(max = 50)
	private String firstName;

	/**
	 * Middle initial.
	 * 
	 * Optional; maximum length of 1.
	 */
	@Column(nullable = true, length = 1)
	@Size(max = 1)
	private String middleInitial;

	/**
	 * Last name; required.
	 * 
	 * Maximum length of 50.
	 */
	@Column(nullable = false, length = 50)
	@NotNull
	@NotEmpty
	@Size(max = 50)
	private String lastName;

	/**
	 * Birth date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "birth_date")
	@Past
	private Date birthDate;

	/**
	 * Primary e-mail address; required.
	 * 
	 * Can not be null or empty. Maximum length of 100 characters.
	 */
	@Column(length = 100)
	@NotNull
	@NotEmpty
	@Size(max = 100)
	private String primaryEmailAddress;

	/**
	 * Secondary e-mail address.
	 * 
	 * Optional. Maximum length of 100 characters.
	 */
	@Column(length = 100)
	@Size(max = 100)
	private String secondaryEmailAddress;

	/**
	 * User name.
	 * 
	 * Maximum length of 25.
	 */
	@Column(length = 25)
	@Size(max = 25)
	private String username;

	/**
	 * User Id Secondary Id for used to identify the user in secondary systems
	 * like LDAP.
	 * 
	 * Maximum length of 25.
	 */
	@Column(length = 25)
	@Size(max = 25)
	private String userId;

	/**
	 * Home phone number.
	 * 
	 * Maximum length of 25.
	 */
	@Column(length = 25)
	@Size(max = 25)
	private String homePhone;

	/**
	 * Work phone number.
	 * 
	 * Maximum length of 25.
	 */
	@Column(length = 25)
	@Size(max = 25)
	private String workPhone;

	/**
	 * Cell (mobile) phone number.
	 * 
	 * Maximum length of 25.
	 */
	@Column(length = 25)
	@Size(max = 25)
	private String cellPhone;

	/**
	 * Address line 1.
	 * 
	 * Maximum length of 50.
	 */
	@Column(length = 50, name = "address_line_1")
	@Size(max = 50)
	private String addressLine1;

	/**
	 * Address line 2.
	 * 
	 * Maximum length of 50.
	 */
	@Column(length = 50, name = "address_line_2")
	@Size(max = 50)
	private String addressLine2;

	/**
	 * City.
	 * 
	 * Maximum length of 50.
	 */
	@Column(length = 50)
	@Size(max = 50)
	private String city;

	/**
	 * State code (abbreviated to 2 characters).
	 * 
	 * Maximum length of 2.
	 */
	@Column(length = 2)
	@Size(max = 2)
	private String state;

	/**
	 * ZIP/postal code.
	 * 
	 * Maximum length of 10.
	 */
	@Column(length = 10)
	@Size(max = 10)
	private String zipCode;

	/**
	 * Photo URL.
	 * 
	 * Maximum length of 100.
	 */
	@Column(length = 100)
	@Size(max = 100)
	private String photoUrl;

	/**
	 * School identifier for the student. A.k.a. Student ID.
	 * 
	 * Maximum length of 50.
	 */
	@Column(length = 50)
	@Size(max = 50)
	private String schoolId;

	/**
	 * Marks the user account able to authenticate in the system.
	 * 
	 * Usually only marked false for former administrators, counselors, and
	 * non-students who no longer use the system anymore.
	 */
	@Nullable()
	private boolean enabled;

	/**
	 * Demographics about a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@ManyToOne()
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "person_demographics_id", unique = true, nullable = true)
	private PersonDemographics demographics;

	/**
	 * Education goal for a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@ManyToOne()
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "person_education_goal_id", unique = true, nullable = true)
	private PersonEducationGoal educationGoal;

	/**
	 * Education plan for a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@ManyToOne()
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "person_education_plan_id", unique = true, nullable = true)
	private PersonEducationPlan educationPlan;

	/**
	 * Education Levels for a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.SAVE_UPDATE })
	private Set<PersonEducationLevel> educationLevels;

	/**
	 * Any funding sources for a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	private Set<PersonFundingSource> fundingSources;

	/**
	 * Any Challenges for a student.
	 * 
	 * Should be null for non-student users.
	 */
	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	private Set<PersonChallenge> challenges;

	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.SAVE_UPDATE })
	private Set<PersonTool> tools;

	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.SAVE_UPDATE })
	private Set<PersonConfidentialityDisclosureAgreement> confidentialityDisclosureAgreements;

	@Nullable()
	@OneToMany(mappedBy = "person", orphanRemoval = true)
	@Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.SAVE_UPDATE })
	private Set<Task> tasks;
	/**
	 * Strengths
	 * 
	 * Maximum length of 4000.
	 */
	@Column(length = 4000)
	@Size(max = 4000)
	private String strengths;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "coach_id", nullable = true)
	private Person coach;

	/**
	 * Initialize a Person.
	 * 
	 * Does not generated an ID, but does initialize empty sets.
	 */
	public Person() {
		super();
		challenges = new HashSet<PersonChallenge>();
		fundingSources = new HashSet<PersonFundingSource>();
		educationLevels = new HashSet<PersonEducationLevel>();
	}

	/**
	 * Initialize a Person with the specified ID and empty sets.
	 * 
	 * @param id
	 *            Identifier
	 */
	public Person(final UUID id) {
		super();
		setId(id);
		challenges = new HashSet<PersonChallenge>();
		fundingSources = new HashSet<PersonFundingSource>();
		educationLevels = new HashSet<PersonEducationLevel>();
	}

	/**
	 * Gets the full name
	 * 
	 * @return The first name and last name concatenated with a space in
	 *         between.
	 */
	public String getFullName() {
		return firstName + " " + lastName;
	}

	/**
	 * Gets the e-mail address with the full name in the standard full e-mail
	 * address syntax.
	 * 
	 * @return The e-mail address with the full name.
	 */
	public String getEmailAddressWithName() {
		return getFullName() + " <" + primaryEmailAddress + ">";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(@NotNull final String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(final String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(@NotNull final String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate == null ? null : new Date(birthDate.getTime());
	}

	public void setBirthDate(final Date birthDate) {
		this.birthDate = birthDate == null ? null : new Date(
				birthDate.getTime());
	}

	public String getPrimaryEmailAddress() {
		return primaryEmailAddress;
	}

	public void setPrimaryEmailAddress(@NotNull final String primaryEmailAddress) {
		this.primaryEmailAddress = primaryEmailAddress;
	}

	public String getSecondaryEmailAddress() {
		return secondaryEmailAddress;
	}

	public void setSecondaryEmailAddress(final String secondaryEmailAddress) {
		this.secondaryEmailAddress = secondaryEmailAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(final String homePhone) {
		this.homePhone = homePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(final String workPhone) {
		this.workPhone = workPhone;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(final String cellPhone) {
		this.cellPhone = cellPhone;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(final String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(final String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(final String photoUrl) {
		this.photoUrl = photoUrl;
	}

	/**
	 * Gets the SchoolID (a.k.a. Student ID given by the school)
	 * 
	 * @return the SchoolID
	 */
	public String getSchoolId() {
		return schoolId;
	}

	/**
	 * Sets the SchoolID (a.k.a. Student ID given by the school)
	 * 
	 * @param schoolId
	 *            the SchoolID (a.k.a. Student ID given by the school); maximum
	 *            length of 50 characters
	 */
	public void setSchoolId(final String schoolId) {
		this.schoolId = schoolId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public PersonDemographics getDemographics() {
		return demographics;
	}

	public void setDemographics(final PersonDemographics demographics) {
		this.demographics = demographics;
	}

	public PersonEducationGoal getEducationGoal() {
		return educationGoal;
	}

	public void setEducationGoal(final PersonEducationGoal educationGoal) {
		this.educationGoal = educationGoal;
	}

	public PersonEducationPlan getEducationPlan() {
		return educationPlan;
	}

	public void setEducationPlan(final PersonEducationPlan educationPlan) {
		this.educationPlan = educationPlan;
	}

	public Set<PersonEducationLevel> getEducationLevels() {
		return educationLevels;
	}

	public void setEducationLevels(
			final Set<PersonEducationLevel> educationLevels) {
		this.educationLevels = educationLevels;
	}

	public Set<PersonFundingSource> getFundingSources() {
		return fundingSources;
	}

	public void setFundingSources(final Set<PersonFundingSource> fundingSources) {
		this.fundingSources = fundingSources;
	}

	public Set<PersonChallenge> getChallenges() {
		return challenges;
	}

	public void setChallenges(final Set<PersonChallenge> challenges) {
		this.challenges = challenges;
	}

	public Set<PersonTool> getTools() {
		return tools;
	}

	public void setTools(final Set<PersonTool> tools) {
		this.tools = tools;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		if ((userId != null) && (userId.length() > 25)) {
			throw new IllegalArgumentException(
					"UserId must be 25 or fewer characters.");
		}

		this.userId = userId;
	}

	public Set<PersonConfidentialityDisclosureAgreement> getConfidentialityDisclosureAgreements() {
		return confidentialityDisclosureAgreements;
	}

	public void setConfidentialityDisclosureAgreements(
			final Set<PersonConfidentialityDisclosureAgreement> confidentialityDisclosureAgreements) {
		this.confidentialityDisclosureAgreements = confidentialityDisclosureAgreements;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(final Set<Task> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the strengths
	 */
	public String getStrengths() {
		return strengths;
	}

	/**
	 * @param strengths
	 *            the strengths to set
	 */
	public void setStrengths(final String strengths) {
		this.strengths = strengths;
	}

	public Person getCoach() {
		return coach;
	}

	public void setCoach(final Person coach) {
		this.coach = coach;
	}

	@Override
	protected int hashPrime() {
		return 3;
	};

	@Override
	public int hashCode() { // NOPMD by jon.adams on 5/9/12 7:25 PM
		int result = hashPrime();

		// Auditable properties
		result *= getId() == null ? "id".hashCode() : getId().hashCode();
		result *= getObjectStatus() == null ? hashPrime() : getObjectStatus()
				.hashCode();

		// Person
		result *= StringUtils.isEmpty(firstName) ? "firstName".hashCode()
				: firstName.hashCode();
		result *= StringUtils.isEmpty(middleInitial) ? "middleInitial"
				.hashCode() : middleInitial.hashCode();
		result *= StringUtils.isEmpty(lastName) ? "middleInitial".hashCode()
				: lastName.hashCode();
		result *= birthDate == null ? "birthDate".hashCode() : birthDate
				.hashCode();
		result *= StringUtils.isEmpty(primaryEmailAddress) ? "primaryEmailAddress"
				.hashCode()
				: primaryEmailAddress.hashCode();
		result *= StringUtils.isEmpty(secondaryEmailAddress) ? "secondaryEmailAddress"
				.hashCode()
				: secondaryEmailAddress.hashCode();
		result *= StringUtils.isEmpty(username) ? "primaryEmailAddress"
				.hashCode() : primaryEmailAddress.hashCode();
		result *= StringUtils.isEmpty(userId) ? "userId".hashCode() : userId
				.hashCode();
		result *= StringUtils.isEmpty(homePhone) ? "homePhone".hashCode()
				: homePhone.hashCode();
		result *= StringUtils.isEmpty(workPhone) ? "workPhone".hashCode()
				: workPhone.hashCode();
		result *= StringUtils.isEmpty(cellPhone) ? "cellPhone".hashCode()
				: cellPhone.hashCode();
		result *= StringUtils.isEmpty(addressLine1) ? "addressLine1".hashCode()
				: addressLine1.hashCode();
		result *= StringUtils.isEmpty(addressLine2) ? "addressLine2".hashCode()
				: addressLine2.hashCode();
		result *= StringUtils.isEmpty(city) ? "city".hashCode() : city
				.hashCode();
		result *= StringUtils.isEmpty(state) ? "state".hashCode() : state
				.hashCode();
		result *= StringUtils.isEmpty(zipCode) ? "zipCode".hashCode() : zipCode
				.hashCode();
		result *= StringUtils.isEmpty(photoUrl) ? "photoUrl".hashCode()
				: photoUrl.hashCode();
		result *= StringUtils.isEmpty(schoolId) ? "schoolId".hashCode()
				: schoolId.hashCode();
		result *= StringUtils.isEmpty(strengths) ? "strengths".hashCode()
				: strengths.hashCode();
		result *= coach == null ? "coach".hashCode() : coach.getId().hashCode();
		// not all fields included. only the business or non-expensive set
		// fields are included in the hashCode

		return result;
	}

	@Override
	public String toString() {
		return "Name: \"" + firstName + " " + lastName + "\" Id: "
				+ super.toString();
	}
}