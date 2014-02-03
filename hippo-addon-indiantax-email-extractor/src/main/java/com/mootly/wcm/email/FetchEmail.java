package com.mootly.wcm.email;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SuppressWarnings("all")
public class FetchEmail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Here I am initializing the xml.
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"spring-context.xml");

		EmailReciever mailReciever = (EmailReciever) context
				.getBean("emailReciever");
		MailParameters mailParam = (MailParameters) context
				.getBean("mailParameters");

		mailReciever.ProcessAttachments(mailParam.hostName, mailParam.userName,
				mailParam.passWord);
	}
}
