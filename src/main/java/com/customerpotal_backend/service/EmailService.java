package com.customerpotal_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value
	("${spring.mail.username}") private String sender;
	
    @Value
    ("${email.otp.subject}") private String emailSubject;

    @Value
    ("${email.otp.message}") private String emailMessage;

	
	public String sendOtpEmail(String email, String otp) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			
			String message = String.format(emailMessage, otp);
			
			mailMessage.setFrom(sender);
			mailMessage.setTo(email);
			mailMessage.setText(message);
			mailMessage.setSubject(emailSubject);
			
//			javaMailSender.send(mailMessage);
			
			return "Mail sent Successfully";
		}catch (Exception e) {
			
		}
		return "Error while sending mail!!!";
	}
}
