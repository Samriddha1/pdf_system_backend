package com.pdf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailService {

	@Autowired
	JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String fromEmail;

	public String sendMail(String email, String bodyText) {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(fromEmail);
			mimeMessageHelper.setTo(email);
			mimeMessageHelper.setSubject("Mail For Reset Password");
			mimeMessageHelper.setText(bodyText, false);
			javaMailSender.send(mimeMessage);
			log.info("Message Sent Successfully to: {}");
		} catch (Exception e) {
			log.error("sendEmail() | Error : {}", e.getMessage());
			throw new RuntimeException(e);
		}
		return null;
	}

}
