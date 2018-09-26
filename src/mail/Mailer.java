package mail;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;

import com.sun.mail.imap.IMAPFolder;

import util.Settings;

public class Mailer {

	private static boolean sendSimpleMailSMTPInternal(String smtpHost, int smtpPort, String smtpUser, String smtpPassword,
			boolean smtpAuth, boolean smtpSecure, boolean trustAllCertificates, String from, String to, String subject, String body, String header) {
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.port", smtpPort);
			props.put("mail.smtp.auth", smtpAuth);
			if(smtpSecure) {
				props.put("mail.smtp.starttls.enable", "true");
			}
			if(trustAllCertificates) {
			    props.put("mail.smtp.ssl.trust", "*");
			}

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(smtpUser, smtpPassword);
				}
			});
			session.setDebug(Settings.debugProtocols);
			// session.setDebug(true);
			MimeMessage msg = new MimeMessage(session);
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			if(header != null && !header.equals("") && header.contains(":") && header.length()>3) {
				String headerName	= header.substring(0, header.indexOf(":"));
				String headerValue	= header.substring(header.indexOf(":"));
				msg.addHeader(headerName, headerValue);				
			}
			msg.setFrom(new InternetAddress(from, from));
			msg.setReplyTo(InternetAddress.parse(from, false));
			msg.setSubject(subject, "UTF-8");
			msg.setText(body, "UTF-8");
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Store connectIMAP(String imapHost, int imapPort, boolean imapSecure, boolean trustAllCertificates,
			String imapUser, String imapPassword) {
		Store store = null;
		Properties props = System.getProperties();
		String protcol = imapSecure ? "imaps" : "imap";
		if (trustAllCertificates) {
			props.put("mail.imaps.ssl.trust", "*");
		}
		props.put("mail.store.protocol", protcol);
		props.put("mail.imap.port", imapPort);
		Session session = Session.getInstance(props);
		session.setDebug(Settings.debugProtocols);
		try {
			store = session.getStore(protcol);
			store.connect(imapHost, imapUser, imapPassword);
		} catch (Exception e) {
			System.err.println("Could not perform IMAP connect to "+imapHost);
			System.err.println(e.getMessage());
			System.exit(4);
		}
		return store;
	}

	public static ArrayList<Mail> listMessagesIMAP(String imapHost, int imapPort, boolean imapSecure, String imapUser,
			String imapPassword, String imapFolderName, boolean trustAllCertificates) {
		Store store = null;
		ArrayList<Mail> messagesR = new ArrayList<Mail>();
		try {
			store = connectIMAP(imapHost, imapPort, imapSecure, trustAllCertificates, imapUser, imapPassword);
			IMAPFolder folder = (IMAPFolder) store.getFolder(imapFolderName);
		    folder.open(Folder.READ_ONLY);
		      Message[] messages = folder.getMessages();
		      for (int i = 0, n = messages.length; i < n; i++) {
		         Message message = messages[i];
		         Mail mo = new Mail(message.getFrom()[0].toString(), message.getReplyTo().toString(), message.getSubject(), getTextFromMessage(message));
		         messagesR.add(mo);
		      }
		      folder.close(false);
		      store.close();
			return messagesR;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.toString());
		
		} finally {
			try {
				if (store != null) {
					store.close();
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return  messagesR;
	}
	
	private static void clearFolderIMAPInternal(String imapHost, int imapPort, boolean imapSecure, boolean trustAllCertificates, String imapUser, String imapPassword, String imapFolderName) {
		Store store = null;
		try {
			store = connectIMAP(imapHost, imapPort, imapSecure, trustAllCertificates, imapUser, imapPassword);
			IMAPFolder folder = (IMAPFolder) store.getFolder(imapFolderName);
			if (!folder.isOpen()) {
				folder.open(Folder.READ_WRITE);
			}
			long largestUid = folder.getUIDNext() - 1;
			int chunkSize = 500;
			for (long offset = 0; offset < largestUid; offset += chunkSize) {
				long start = Math.max(1, largestUid - offset - chunkSize + 1);
				long end = Math.max(1, largestUid - offset);
				Message[] messages = folder.getMessagesByUID(start, end);
				for (Message message : messages) {
					message.setFlag(Flags.Flag.DELETED, true);
				}
			}
			folder.close(true);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(store!=null) {
					store.close();	
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}


	private static String getTextFromMessage(Message message) throws Exception {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		} else if (message.isMimeType("multipart/*")) {
			String result = "";
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break; // without break same text appears twice in my tests
				} else if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					result = result + "\n" + Jsoup.parse(html).text();
				}
			}
			return result;
		}
		return "";
	}

	public static void clearFolderIMAP(MailAccount mailAccount) {
		clearFolderIMAPInternal(mailAccount.getHostImap(), mailAccount.getPortImap(), mailAccount.isSecureImap(), mailAccount.isTrustAllCerts(), mailAccount.getLogin(), mailAccount.getPw(), mailAccount.getInboxFolderName());
	}

	public static void send(MailAccount from, MailAccount to, String subject, String content) {
		sendSimpleMailSMTPInternal(from.getHostSmtp(), from.getPortSmtp(), from.getLogin(), from.getPw(), true, from.isSecureSmtp(), from.isTrustAllCerts(), from.getAddress(), to.getAddress(), subject, content, null);		
	}

	public static void send(MailAccount from, MailAccount to, String subject, String content, String header) {
		sendSimpleMailSMTPInternal(from.getHostSmtp(), from.getPortSmtp(), from.getLogin(), from.getPw(), true, from.isSecureSmtp(), from.isTrustAllCerts(), from.getAddress(), to.getAddress(), subject, content, header);
	}

	public static ArrayList<Mail> getMessages(MailAccount mailAccount) {
		ArrayList<Mail> mailsReceived = Mailer.listMessagesIMAP(mailAccount.getHostImap(), mailAccount.getPortImap(), mailAccount.isSecureImap(), mailAccount.getLogin(), mailAccount.getPw(), mailAccount.getInboxFolderName(), mailAccount.isTrustAllCerts());
		System.out.println(Settings.indentMarker+mailAccount.getAccountName()+" received "+mailsReceived.size()+" mails");
		return mailsReceived;
	}
}
