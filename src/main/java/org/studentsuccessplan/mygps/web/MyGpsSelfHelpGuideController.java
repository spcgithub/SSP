package org.studentsuccessplan.mygps.web;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.studentsuccessplan.mygps.business.SelfHelpGuideManager;
import org.studentsuccessplan.mygps.model.transferobject.SelfHelpGuideContentTO;
import org.studentsuccessplan.ssp.transferobject.reference.SelfHelpGuideTO;

@Controller
@RequestMapping("/mygps/selfhelpguide")
public class MyGpsSelfHelpGuideController extends AbstractMyGpsController {

	@Autowired
	private SelfHelpGuideManager selfHelpGuideManager;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MyGpsSelfHelpGuideController.class);

	public void setSelfHelpGuideManager(
			SelfHelpGuideManager selfHelpGuideManager) {
		this.selfHelpGuideManager = selfHelpGuideManager;
	}

	@RequestMapping(value = "/getAll", method = RequestMethod.GET)
	public @ResponseBody
	List<SelfHelpGuideTO> getAll() throws Exception {

		try {
			return SelfHelpGuideTO.listToTOList(selfHelpGuideManager.getAll());
		} catch (Exception e) {
			LOGGER.error("ERROR : getAll() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value = "/getContentById", method = RequestMethod.GET)
	public @ResponseBody
	SelfHelpGuideContentTO getContentById(
			@RequestParam("selfHelpGuideId") UUID selfHelpGuideId)
			throws Exception {

		try {
			return selfHelpGuideManager.getContentById(selfHelpGuideId);
		} catch (Exception e) {
			LOGGER.error("ERROR : getContentById() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value = "/getBySelfHelpGuideGroup", method = RequestMethod.GET)
	public @ResponseBody
	List<SelfHelpGuideTO> getBySelfHelpGuideGroup(
			@RequestParam("selfHelpGuideGroupId") UUID selfHelpGuideGroupId)
			throws Exception {

		try {
			return SelfHelpGuideTO.listToTOList(selfHelpGuideManager
					.getBySelfHelpGuideGroup(selfHelpGuideGroupId));
		} catch (Exception e) {
			LOGGER.error("ERROR : getBySelfHelpGuideGroup() : {}",
					e.getMessage(), e);
			throw e;
		}
	}
}