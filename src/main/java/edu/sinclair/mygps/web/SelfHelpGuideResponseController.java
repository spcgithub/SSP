package edu.sinclair.mygps.web;

import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.sinclair.mygps.business.SelfHelpGuideManager;
import edu.sinclair.mygps.model.transferobject.SelfHelpGuideResponseTO;

@Controller
@RequestMapping("/selfhelpguideresponse")
public class SelfHelpGuideResponseController {

	@Autowired
	private SelfHelpGuideManager manager;

	private Logger logger = LoggerFactory.getLogger(SelfHelpGuideResponseController.class);

	// Needed for tests
	public void setManager(SelfHelpGuideManager manager) {
		this.manager = manager;
	}

	@RequestMapping(value="cancel", method = RequestMethod.GET)
	public @ResponseBody
	boolean cancel(
			@RequestParam("selfHelpGuideResponseId") UUID selfHelpGuideResponseId)
					throws Exception {

		try {
			return manager.cancelSelfHelpGuideResponse(selfHelpGuideResponseId);
		} catch (Exception e) {
			logger.error("ERROR : cancel() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value="complete", method = RequestMethod.GET)
	public @ResponseBody
	boolean complete(
			@RequestParam("selfHelpGuideResponseId") UUID selfHelpGuideResponseId)
					throws Exception {

		try {
			return manager.completeSelfHelpGuideResponse(selfHelpGuideResponseId);
		} catch (Exception e) {
			logger.error("ERROR : complete() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value="getById", method = RequestMethod.GET)
	public @ResponseBody
	SelfHelpGuideResponseTO getById(
			@RequestParam("selfHelpGuideResponseId") UUID selfHelpGuideResponseId)
					throws Exception {

		try {
			return manager.getSelfHelpGuideResponseById(selfHelpGuideResponseId);
		} catch (Exception e) {
			logger.error("ERROR : getById() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value="initiate", method = RequestMethod.GET)
	public @ResponseBody
	String initiate(@RequestParam("selfHelpGuideId") UUID selfHelpGuideId)
			throws Exception {

		try {
			return manager.initiateSelfHelpGuideResponse(selfHelpGuideId)
					.toString();
		} catch (Exception e) {
			logger.error("ERROR : initiate() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@RequestMapping(value="answer", method = RequestMethod.GET)
	public @ResponseBody
	boolean answer(
			@RequestParam("selfHelpGuideResponseId") UUID selfHelpGuideResponseId,
			@RequestParam("selfHelpGuideQuestionId") UUID selfHelpGuideQuestionId,
			@RequestParam("response") boolean response) throws Exception {

		try {
			return manager.answerSelfHelpGuideQuestion(selfHelpGuideResponseId, selfHelpGuideQuestionId, response);
		} catch (Exception e) {
			logger.error("ERROR : answer() : {}", e.getMessage(), e);
			throw e;
		}
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody String handleException(Exception e, HttpServletResponse response) {
		logger.error("ERROR : handleException()", e);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return e.getMessage();
	}

}