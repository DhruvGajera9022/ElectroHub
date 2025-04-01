package com.example.swiftmart.EmailService;

import android.os.AsyncTask;

public class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
	private String recipientEmail;
	private String subject;
	private String messageBody;

	public SendEmailTask(String recipientEmail, String subject, String messageBody) {
		this.recipientEmail = recipientEmail;
		this.subject = subject;
		this.messageBody = messageBody;
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		try {
			JavaMailAPI javaMailAPI = new JavaMailAPI("aarugaming2005@gmail.com", "wctw rnfq olyj cmim");
			return javaMailAPI.sendEmail(recipientEmail, subject, messageBody);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean isSuccess) {
		if (isSuccess) {
			System.out.println("Email sent successfully!");
		} else {
			System.out.println("Failed to send email.");
		}
	}
}
