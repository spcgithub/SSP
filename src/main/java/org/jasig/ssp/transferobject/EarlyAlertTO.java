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
package org.jasig.ssp.transferobject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jasig.ssp.model.EarlyAlert;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.transferobject.reference.EarlyAlertReasonTO;
import org.jasig.ssp.transferobject.reference.EarlyAlertSuggestionTO;

/**
 * Early Alert transfer object
 * 
 * @author jon.adams
 */
public class EarlyAlertTO extends AbstractAuditableTO<EarlyAlert> implements
		TransferObject<EarlyAlert>, Serializable {

	private static final long serialVersionUID = -3197180145189755870L;

	private String courseName;

	private String courseTitle;

	private String courseTermCode;

	private String emailCC;

	private UUID campusId;

	private String earlyAlertReasonOtherDescription;

	private String earlyAlertSuggestionOtherDescription;

	private String comment;

	private UUID personId;

	private Date closedDate;

	private UUID closedById;

	private String closedByName;
	
	private Set<EarlyAlertReasonTO> earlyAlertReasonIds;

	private Set<EarlyAlertSuggestionTO> earlyAlertSuggestionIds;

	private Boolean sendEmailToStudent = Boolean.FALSE;

	/**
	 * Empty constructor
	 */
	public EarlyAlertTO() {
		super();
	}

	/**
	 * Construct a transfer object based on the specified model.
	 * 
	 * @param earlyAlert
	 *            Model to copy from
	 */
	public EarlyAlertTO(final EarlyAlert earlyAlert) {
		super();
		from(earlyAlert);
	}

	@Override
	public final void from(final EarlyAlert earlyAlert) {
		super.from(earlyAlert);

		courseName = earlyAlert.getCourseName();
		courseTitle = earlyAlert.getCourseTitle();
		courseTermCode = earlyAlert.getCourseTermCode();
		emailCC = earlyAlert.getEmailCC();
		campusId = earlyAlert.getCampus() == null ? null : earlyAlert
				.getCampus().getId();
		earlyAlertReasonOtherDescription = earlyAlert
				.getEarlyAlertReasonOtherDescription();
		earlyAlertSuggestionOtherDescription = earlyAlert
				.getEarlyAlertSuggestionOtherDescription();
		comment = earlyAlert.getComment();
		closedDate = earlyAlert.getClosedDate();
		closedById = earlyAlert.getClosedById();

		if ( closedById != null ) {
			Person closedBy = earlyAlert.getClosedBy();
			closedByName = closedBy.getFirstName()
					+ (closedBy.getMiddleName() == null || closedBy.getMiddleName().length() == 0 ? "" : " " + closedBy.getMiddleName())
					+ " " + closedBy.getLastName();
		}
		
		

		personId = earlyAlert.getPerson() == null ? null : earlyAlert
				.getPerson().getId();

		earlyAlertReasonIds = EarlyAlertReasonTO.toTOSet(earlyAlert
				.getEarlyAlertReasonIds());
		earlyAlertSuggestionIds = EarlyAlertSuggestionTO.toTOSet(earlyAlert
				.getEarlyAlertSuggestionIds());
	}

	/**
	 * Convert a collection of models to transfer objects.
	 * 
	 * @param earlyAlerts
	 *            Models to copy
	 * @return Transfer object equivalent of the models
	 */
	public static List<EarlyAlertTO> toTOList(
			final Collection<EarlyAlert> earlyAlerts) {
		final List<EarlyAlertTO> earlyAlertTOs = new ArrayList<EarlyAlertTO>();
		if ((earlyAlerts != null) && !earlyAlerts.isEmpty()) {
			for (final EarlyAlert earlyAlert : earlyAlerts) {
				earlyAlertTOs.add(new EarlyAlertTO(earlyAlert)); // NOPMD
			}
		}

		return earlyAlertTOs;
	}

	/**
	 * @return the Course Name
	 */
	public String getCourseName() {
		return courseName;
	}

	/**
	 * @param courseName
	 *            the Course Name to set
	 */
	public void setCourseName(final String courseName) {
		this.courseName = courseName;
	}

	/**
	 * @return the Course Title
	 */
	public String getCourseTitle() {
		return courseTitle;
	}

	/**
	 * @param courseTitle
	 *            the Course Title to set
	 */
	public void setCourseTitle(final String courseTitle) {
		this.courseTitle = courseTitle;
	}

	/**
	 * @return the Course Term Code
	 */
	public String getCourseTermCode() {
		return courseTermCode;
	}

	/**
	 * @param courseTermCode
	 *            the Course Term Code to set
	 */
	public void setCourseTermCode(final String courseTermCode) {
		this.courseTermCode = courseTermCode;
	}

	/**
	 * @return the Email CC field, if any
	 */
	public String getEmailCC() {
		return emailCC;
	}

	/**
	 * @param emailCC
	 *            the Email CC field to set
	 */
	public void setEmailCC(final String emailCC) {
		this.emailCC = emailCC;
	}

	/**
	 * @return the campusId
	 */
	public UUID getCampusId() {
		return campusId;
	}

	/**
	 * @param campusId
	 *            the campusId to set
	 */
	public void setCampusId(final UUID campusId) {
		this.campusId = campusId;
	}

	/**
	 * @return the ReasonOtherDescription
	 */
	public String getEarlyAlertReasonOtherDescription() {
		return earlyAlertReasonOtherDescription;
	}

	/**
	 * @param earlyAlertReasonOtherDescription
	 *            the ReasonOtherDescription to set
	 */
	public void setEarlyAlertReasonOtherDescription(
			final String earlyAlertReasonOtherDescription) {
		this.earlyAlertReasonOtherDescription = earlyAlertReasonOtherDescription;
	}

	/**
	 * @return the SuggestionOtherDescription
	 */
	public String getEarlyAlertSuggestionOtherDescription() {
		return earlyAlertSuggestionOtherDescription;
	}

	/**
	 * @param earlyAlertSuggestionOtherDescription
	 *            the SuggestionOtherDescription to set
	 */
	public void setEarlyAlertSuggestionOtherDescription(
			final String earlyAlertSuggestionOtherDescription) {
		this.earlyAlertSuggestionOtherDescription = earlyAlertSuggestionOtherDescription;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the person identifier
	 * 
	 * @return The person identifier
	 */
	public UUID getPersonId() {
		return personId;
	}

	/**
	 * Person identifier
	 * 
	 * @param personId
	 *            Person identifier
	 */
	public void setPersonId(final UUID personId) {
		this.personId = personId;
	}

	/**
	 * @return the closedDate
	 */
	public Date getClosedDate() {
		return closedDate == null ? null : new Date(closedDate.getTime());
	}

	/**
	 * @param closedDate
	 *            the closedDate to set
	 */
	public void setClosedDate(final Date closedDate) {
		this.closedDate = closedDate == null ? null : new Date(
				closedDate.getTime());
	}

	/**
	 * @return the closedById
	 */
	public UUID getClosedById() {
		return closedById;
	}

	/**
	 * @param closedById
	 *            the closedById to set
	 */
	public void setClosedById(final UUID closedById) {
		this.closedById = closedById;
	}

	public String getClosedByName() {
		return closedByName;
	}

	public void setClosedByName(String closedByName) {
		this.closedByName = closedByName;
	}

	/**
	 * @return The list of EarlyAlertReasons
	 */
	public Set<EarlyAlertReasonTO> getEarlyAlertReasonIds() {
		return earlyAlertReasonIds;
	}

	/**
	 * @param earlyAlertReasonIds
	 *            The list of EarlyAlertReasons to set
	 */
	public void setEarlyAlertReasonIds(
			final Set<EarlyAlertReasonTO> earlyAlertReasonIds) {
		this.earlyAlertReasonIds = earlyAlertReasonIds;
	}

	/**
	 * @return The list of EarlyAlertSuggestions
	 */
	public Set<EarlyAlertSuggestionTO> getEarlyAlertSuggestionIds() {
		return earlyAlertSuggestionIds;
	}

	/**
	 * @param earlyAlertSuggestionIds
	 *            The list of EarlyAlertSuggestions to set
	 */
	public void setEarlyAlertSuggestionIds(
			final Set<EarlyAlertSuggestionTO> earlyAlertSuggestionIds) {
		this.earlyAlertSuggestionIds = earlyAlertSuggestionIds;
	}

	/**
	 * For the create API method, if true, will send a message to the student.
	 * 
	 * @return If true, will send a message to student for the created Early
	 *         Alert.
	 */
	public Boolean getSendEmailToStudent() {
		return sendEmailToStudent;
	}

	/**
	 * @param sendEmailToStudent
	 *            If true, will send a message to student for the created Early
	 *            Alert. Null values will default to false.
	 */
	public void setSendEmailToStudent(final Boolean sendEmailToStudent) {
		this.sendEmailToStudent = sendEmailToStudent;
	}

}