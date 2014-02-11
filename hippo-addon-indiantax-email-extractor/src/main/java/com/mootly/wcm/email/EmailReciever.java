package com.mootly.wcm.email;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class EmailReciever {
	private static final Logger log = LoggerFactory
			.getLogger(EmailReciever.class);

	public static void ProcessAttachments(String hostName, String userName,
			String passWord) {
		Properties props = new Properties();
		props.setProperty("mail.store.protocol", "imaps");
		try {

			Session session = Session.getInstance(props, null);
			Store store = session.getStore();
			store.connect(hostName, userName, passWord);
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			Message[] msg = inbox.getMessages();

			for (int n = msg.length - 1; n > 0; n--) {

				String contenType = msg[n].getContentType();
				if (log.isInfoEnabled()) {
					log.info("SUBJECT:" + msg[n].getSubject());
				}

				if (msg[n].isSet(Flag.SEEN)) {
					if (log.isInfoEnabled()) {
						log.info("it is already seen");
					}

				} else {
					if (contenType.contains("multipart")) {
						Multipart multipart = (Multipart) msg[n].getContent();
						handleMultiPart(multipart);
					} else {
						if (log.isInfoEnabled()) {
							log.info("not multipart");
						}

					}
					msg[n].setFlag(Flag.SEEN, true);
				}
			}
			inbox.close(false);
			store.close();
		} catch (MessagingException msgEx) {
			msgEx.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void handleMultiPart(Multipart multipart) {
		try {
			for (int i = 0; i < multipart.getCount(); i++) {
				if (log.isInfoEnabled()) {
					log.info("it is a  multipart");
				}

				MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
				handlEachPart(part);
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void handlEachPart(MimeBodyPart bodyPart) {
		// System.out.println("SUBJECT:" + msg[i].getSubject());
		try {
			if (log.isInfoEnabled()) {
				log.info("CONTENT:" + bodyPart.getContentType());
				log.info("Attachment:::" + bodyPart.ATTACHMENT);
			}

			if (bodyPart.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
				if (bodyPart.getContentType().contains("ZIP")) {
					System.out.println("it is zip");
					String tmpDir = System.getProperty("java.io.tmpdir");
					String tmpPathToAttachment = tmpDir + "/" + "attachments";
					new File(tmpPathToAttachment).mkdir();
					bodyPart.saveFile(tmpPathToAttachment + "/"
							+ bodyPart.getFileName());
					ZipFile zFile = new ZipFile(tmpPathToAttachment + "/"
							+ bodyPart.getFileName());
					unZipAttachment(ReadZipFile(zFile));
					new File(tmpPathToAttachment + "/" + bodyPart.getFileName())
							.delete();
				} else {
					if (log.isInfoEnabled()) {
						log.info("attachment is not a zip");
					}

				}
			} else {
				if (log.isInfoEnabled()) {
					log.info("No Attachment");
				}

			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// unzipping the attachments inside the UnZippedDocuments in temp directory
	public static void unZipAttachment(ZipFile zipFile) throws ZipException {
		String destPath = System.getProperty("java.io.tmpdir");
		String ZippedDocuments = destPath + "/" + "UnZippedDocuments";
		new File(ZippedDocuments).mkdir();

		if (zipFile != null) {
			zipFile.extractAll(ZippedDocuments);
		}

	}

	public static ZipFile ReadZipFile(ZipFile zipFile) {
		return zipFile;
	}
}
