package edu.sinclair.ssp.service.impl;

import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.sinclair.ssp.dao.MessageDao;
import edu.sinclair.ssp.model.Message;
import edu.sinclair.ssp.model.Person;
import edu.sinclair.ssp.service.MessageService;
import edu.sinclair.ssp.service.PersonService;
import edu.sinclair.ssp.service.SecurityService;

@Service
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private PersonService personService;

	@Autowired
	private SecurityService securityService;

	@Value("#{configProperties.messageManager_bcc}")
	private String bcc;

	@Value("#{configProperties.send_mail}")
	private boolean sendMail = false;

	private Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Override
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	@Override
	@Transactional(readOnly = false)
	public void createMessage(Person to, String subject, String body)
			throws Exception {

		Message message = new Message();

		message.setBody(body);

		if (securityService.isAuthenticated()) {
			message.setCreatedBy(securityService.currentlyLoggedInSspUser()
					.getPerson());
		} else {
			message.setCreatedBy(personService
					.get(Person.SYSTEM_ADMINISTRATOR_ID));
		}

		message.setCreatedDate(new Date());
		message.setRecipient(to);

		if (securityService.isAuthenticated()) {
			message.setSender(securityService.currentlyLoggedInSspUser()
					.getPerson());
		} else {
			message.setSender(personService.get(Person.SYSTEM_ADMINISTRATOR_ID));
		}

		message.setSubject(subject);

		messageDao.save(message);
	}

	@Override
	public void createMessage(String to, String subject, String body)
			throws Exception {

		Message message = new Message();

		message.setBody(body);

		if (securityService.isAuthenticated()) {
			message.setCreatedBy(securityService.currentlyLoggedInSspUser()
					.getPerson());
		} else {
			message.setCreatedBy(personService
					.get(Person.SYSTEM_ADMINISTRATOR_ID));
		}

		message.setCreatedDate(new Date());
		message.setRecipientEmailAddress(to);

		if (securityService.isAuthenticated()) {
			message.setSender(securityService.currentlyLoggedInSspUser()
					.getPerson());
		} else {
			message.setSender(personService.get(Person.SYSTEM_ADMINISTRATOR_ID));
		}

		message.setSubject(subject);

		messageDao.save(message);
	}

	@Override
	@Transactional(readOnly = false)
	public void sendQueuedMessages() {

		logger.info("BEGIN : sendQueuedMessages()");

		try {

			List<Message> messages = messageDao.selectQueued();

			for (Message message : messages) {
				this.sendMessage(message);
			}

		} catch (Exception e) {
			logger.error("ERROR : sendQueuedMessages() : {}", e);
		}

		logger.info("END : sendQueuedMessages()");
	}

	protected boolean validateEmail(String email){
		EmailValidator emailValidator = EmailValidator.getInstance();
		return emailValidator.isValid(email);
	}

	@Override
	public boolean sendMessage(Message message) {

		logger.info("BEGIN : sendMessage()");

		logger.info("Sending message: {}", message.getId().toString());

		boolean retVal = true;

		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

			mimeMessageHelper.setFrom(personService.get(
					Person.SYSTEM_ADMINISTRATOR_ID).getEmailAddressWithName());
			mimeMessageHelper.setReplyTo(message.getSender().getEmailAddressWithName());

			if (message.getRecipient() != null) {
				mimeMessageHelper.setTo(message.getRecipient().getEmailAddressWithName());
			} else if (message.getRecipientEmailAddress() != null) {
				mimeMessageHelper.setTo(message.getRecipientEmailAddress());
			} else {
				return false;
			}

			if(!validateEmail(message.getRecipientEmailAddress())){
				throw new Exception("Recipient Email Address '" + message.getRecipientEmailAddress() + "' is invalid");
			}

			if ((this.bcc != null) && (this.bcc.length() > 0)) {
				mimeMessageHelper.setBcc(this.bcc);
			}

			mimeMessageHelper.setSubject(message.getSubject());
			mimeMessageHelper.setText(message.getBody());

			mimeMessage.setContent(message.getBody(), "text/html");

			if(sendMail){
				javaMailSender.send(mimeMessage);
			}

			message.setSentDate(new Date());
			messageDao.save(message);

		} catch (MailException e) {
			retVal = false;
			logger.error("ERROR : sendMessage() : {}", e);
		} catch (MessagingException e) {
			retVal = false;
			logger.error("ERROR : sendMessage() : {}", e);
		} catch (Exception e) {
			retVal = false;
			logger.error("ERROR : sendMessage() : {}", e);
		}

		logger.info("END : sendMessage()");

		return retVal;
	}
}