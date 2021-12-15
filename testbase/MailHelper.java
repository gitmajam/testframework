package com.tribu.qaselenium.testframework.testbase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;

import org.apache.logging.log4j.Logger;

public class MailHelper extends TestBase {
	protected Logger log = TestLoggerFactory.getInstance().getLogger();

	private static String imapHost = "imap.gmail.com";
	private static String imapPort = "993";

	private String userName = null;
	private String password = null;

	public MailHelper(String userName, String password) {

		this.userName = userName;
		this.password = password;
	}

	public Session setIMAPSession() {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");

		// server setting
		props.put("mail.imap.host", imapHost);
		props.put("mail.imap.port", imapPort);

		// SSL setting
		props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		props.setProperty("mail.imap.socketFactory.port", String.valueOf(imapPort));

		return Session.getInstance(props);
	}

	public Message searchEmail(String folderName, String keyword, Date aDate) throws Exception {

		Session session = setIMAPSession();
		Store store = session.getStore("imap");
		store.connect(userName, password);

		Folder folder = store.getFolder(folderName);
		folder.open(Folder.READ_WRITE);

		System.out.println("Total Message:" + folder.getMessageCount());
		System.out.println("Unread Message:" + folder.getUnreadMessageCount());

		Message[] messages = null;
		boolean isMailFound = false;
		Message mailSearching = null;

		// Search for mail
		for (int i = 0; i < 5; i++) {
			messages = folder.search(new SubjectTerm("keyword"), folder.getMessages());
			// Wait for 10 seconds
			if (messages.length == 0) {
				Thread.sleep(10000);
			}
		}
		// veriry for unread mail
		// This is to avoid using the mail for which
		// Registration is already done
		if (!messages[messages.length - 1].isSet(Flags.Flag.SEEN)) {
			mailSearching = messages[messages.length - 1];
			System.out.println("Message Count is: " + mailSearching.getMessageNumber());
			isMailFound = true;
		}

		// Test fails if no unread mail was found
		if (!isMailFound) {
			throw new Exception("Could not find new mail");

			// Read the content of mail and launch registration URL
		} else {
			System.out.println("Email Found");
			return mailSearching;
		}
	}

	public char[] getRegisterCode(String keyword) throws Exception {

		Message mailSearching = searchEmail("INBOX", keyword, new Date());
		
		String line;
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(mailSearching.getInputStream()));
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		
		//get the code from the string
		String registrationURL = buffer.toString().split("\\*")[1];
		log.info(registrationURL);
		return registrationURL.toCharArray();
	}
}
