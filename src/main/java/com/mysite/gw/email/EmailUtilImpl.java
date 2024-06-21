package com.mysite.gw.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.mysite.gw.user.SiteUser;
import com.mysite.gw.user.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailUtilImpl implements EmailUtil {
	private final UserService userService;
	private final EmailService emailService;

	@Autowired
	private JavaMailSender sender;

	public Map<String, Object> sendEmailPw(String username, String subject, String body, String serial) {
		Map<String, Object> result = new HashMap<String, Object>();
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		SiteUser user = userService.getUser(username);
		String toAddress = user.getEmail();
		try {
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(body);
			result.put("resultCode", 200);
		} catch (MessagingException e) {
			e.printStackTrace();
			result.put("resultCode", 500);
		}

		sender.send(message);
		emailService.setRedis(toAddress, serial);
		return result;
	}
	
	public Map<String, Object> sendEmailSign(String email, String subject, String body, String serial) {
		Map<String, Object> result = new HashMap<String, Object>();
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(body);
			result.put("resultCode", 200);
		} catch (MessagingException e) {
			e.printStackTrace();
			result.put("resultCode", 500);
		}

		sender.send(message);
		emailService.setRedis(email, serial);
		return result;
	}
}