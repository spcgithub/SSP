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
package org.jasig.ssp.web.api.reports; // NOPMD

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import org.jasig.ssp.factory.PersonTOFactory;
import org.jasig.ssp.model.ObjectStatus;
import org.jasig.ssp.model.Person;
import org.jasig.ssp.model.external.Term;
import org.jasig.ssp.model.reference.ProgramStatus;
import org.jasig.ssp.security.permissions.Permission;
import org.jasig.ssp.service.ObjectNotFoundException;
import org.jasig.ssp.service.PersonService;
import org.jasig.ssp.service.external.ExternalStudentFinancialAidService;
import org.jasig.ssp.service.external.ExternalStudentTranscriptService;
import org.jasig.ssp.service.external.ExternalStudentTranscriptTermService;
import org.jasig.ssp.service.external.RegistrationStatusByTermService;
import org.jasig.ssp.service.external.TermService;
import org.jasig.ssp.service.reference.ProgramStatusService;
import org.jasig.ssp.service.reference.ReferralSourceService;
import org.jasig.ssp.service.reference.ServiceReasonService;
import org.jasig.ssp.service.reference.SpecialServiceGroupService;
import org.jasig.ssp.service.reference.StudentTypeService;
import org.jasig.ssp.transferobject.PersonTO;
import org.jasig.ssp.transferobject.reports.BaseStudentReportTO;
import org.jasig.ssp.transferobject.reports.PersonSearchFormTO;
import org.jasig.ssp.transferobject.reports.PersonReportTO;
import org.jasig.ssp.util.sort.PagingWrapper;
import org.jasig.ssp.util.sort.SortingAndPaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;

/**
 * Service methods for manipulating data about people in the system.
 * <p>
 * Mapped to URI path <code>report/pretransistioned</code>
 */
@Controller
@RequestMapping("/1/report/pretransitioned")
public class PreTransitionedReportController extends ReportBaseController { // NOPMD
	
	private static final String REPORT_URL_PDF = "/reports/preTransitioned_pdf.jasper";
	private static final String REPORT_URL_CSV = "/reports/preTransitioned_csv.jasper";
	private static final String REPORT_FILE_TITLE = "Counselor_Case_Management_Report";
	


	private static final Logger LOGGER = LoggerFactory
			.getLogger(PreTransitionedReportController.class);

	@Autowired
	private transient PersonService personService;
	@Autowired
	private transient PersonTOFactory personTOFactory;
	@Autowired
	private transient SpecialServiceGroupService ssgService;
	@Autowired
	private transient ReferralSourceService referralSourcesService;
	@Autowired
	private transient ProgramStatusService programStatusService;
	@Autowired
	private transient StudentTypeService studentTypeService;
	
	@Autowired
	protected transient ServiceReasonService serviceReasonService;	
	
	@Autowired
	protected transient ExternalStudentTranscriptService externalStudentTranscriptService;
	
	@Autowired
	protected transient ExternalStudentTranscriptTermService externalStudentTranscriptTermService;
	
	@Autowired
	protected transient TermService termService;
	
	
	@Autowired
	protected transient RegistrationStatusByTermService registrationStatusByTermService;
	
	@Autowired
	protected transient ExternalStudentFinancialAidService externalStudentFinancialAidService;

	// @Autowired
	// private transient PersonTOFactory factory;

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT,
				Locale.US);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

	@RequestMapping(method = RequestMethod.GET)
	@PreAuthorize(Permission.SECURITY_REPORT_READ)
	@ResponseBody
	public void getPreTransitioned(
			final HttpServletResponse response,
			final @RequestParam(required = false) ObjectStatus status,
			final @RequestParam(required = false) UUID coachId,
			final @RequestParam(required = false) UUID programStatus,
			final @RequestParam(required = false) List<UUID> specialServiceGroupIds,
			final @RequestParam(required = false) List<UUID> referralSourcesIds,
			final @RequestParam(required = false) List<UUID> studentTypeIds,
			final @RequestParam(required = false) List<UUID> serviceReasonIds,
			final @RequestParam(required = false) Date createDateFrom,
			final @RequestParam(required = false) Date createDateTo,
			final @RequestParam(required = false) Integer anticipatedStartYear,
			final @RequestParam(required = false) String anticipatedStartTerm,
			final @RequestParam(required = false) String homeDepartment,
			final @RequestParam(required = false) String termCode,
			final @RequestParam(required = false, defaultValue = DEFAULT_REPORT_TYPE) String reportType)
			throws ObjectNotFoundException, JRException, IOException {

		
		final Map<String, Object> parameters = Maps.newHashMap();
		final PersonSearchFormTO personSearchForm = new PersonSearchFormTO();
		
		SearchParameters.addCoach(coachId, parameters, personSearchForm, personService, personTOFactory);
		
		SearchParameters.addReferenceLists(studentTypeIds, 
				specialServiceGroupIds, 
				referralSourcesIds,
				serviceReasonIds,
				parameters, 
				personSearchForm, 
				studentTypeService, 
				ssgService, 
				referralSourcesService,
				serviceReasonService);
		
		SearchParameters.addDateRange(createDateFrom, 
				createDateTo, 
				termCode, 
				parameters, 
				personSearchForm, 
				termService);
		
		SearchParameters.addReferenceTypes(programStatus, 
				null, 
				false,
				null,
				homeDepartment,
				parameters, 
				personSearchForm, 
				programStatusService, 
				null);
		
		SearchParameters.addAnticipatedAndActualStartTerms(anticipatedStartTerm, 
				anticipatedStartYear, 
				null, 
				null, 
				parameters, 
				personSearchForm);

		// TODO Specifying person name sort fields in the SaP doesn't seem to
		// work... end up with empty results need to dig into actual query
		// building
		/*final List<Person> people = personService.peopleFromCriteria(
				personSearchForm, SearchParameters.getReportPersonSortingAndPagingAll(status));
		
		Collections.sort(people, Person.PERSON_NAME_AND_ID_COMPARATOR);
		
		final List<PersonReportTO> peopleReportTOList = PersonReportTO.toPersonTOList(people);*/
		
		final PagingWrapper<BaseStudentReportTO> reports = personService.getStudentReportTOsFromCriteria(
				personSearchForm, SearchParameters.getReportPersonSortingAndPagingAll(status));
		Term currentTerm = termService.getCurrentTerm();
		List<BaseStudentReportTO> compressedReports = this.processStudentReportTOs(reports);
		for(BaseStudentReportTO report:compressedReports){
			report.setStudentTranscript(externalStudentTranscriptService, externalStudentFinancialAidService);
			report.setCurrentRegistrationStatus(registrationStatusByTermService);
			report.setLastTermGPAAndLastTermRegistered(externalStudentTranscriptTermService, currentTerm);
			report.setCurrentProgramStatusCode(ProgramStatus.PROGRAM_STATUS_CODES.get(report.getProgramStatusId().toString()));
		}
		
		SearchParameters.addStudentCount(compressedReports, parameters);

		generateReport(response, parameters, compressedReports, reportType.equals("pdf") ? REPORT_URL_PDF : REPORT_URL_CSV, reportType, REPORT_FILE_TITLE);

	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}
}