package com.example.swiftmart.EmailService;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI {
	private String senderEmail;
	private String senderPassword;

	public JavaMailAPI(String senderEmail, String senderPassword) {
		this.senderEmail = senderEmail;
		this.senderPassword = senderPassword;
	}

	public boolean sendEmail(String recipientEmail, String subject, String messageBody) {
		try {
			// Email properties
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			// Create session
			Session session = Session.getInstance(props, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(senderEmail, senderPassword);
				}
			});

			// Compose email
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(subject);
			message.setText(messageBody);

			// Send email
			Transport.send(message);
			return true; // Email sent successfully
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Email failed
		}
	}
}
