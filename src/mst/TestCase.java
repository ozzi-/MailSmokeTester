package mst;
import java.util.ArrayList;

import mail.Mailer;
import mail.MailAccount;
import mail.Mail;

public class TestCase {
	private int tcn;
	private String testCaseName;
	private MailAccount from;
	private MailAccount to;
	private String subjectID;
	private String subject;
	private String content;
	private String subjectContain;
	private String subjectDoesNotContain;
	private String contentContain;
	private String contentDoesNotContain;
	private String header;
	private boolean sensitivityHeader;
	
	private static int testCaseCounter = 1;

	public TestCase(String testCaseName, MailAccount from, MailAccount to, String subject, String content, String subjectContain, 
			String subjectNotContain, String contentContain, String contentNotContain) {
		testCaseCounter++;
		this.setTestCaseName(testCaseName);
		this.setFrom(from);
		this.setTo(to);
		this.setSubjectID(testCaseName+"("+testCaseCounter+")");
		this.setSubject(testCaseName+"("+testCaseCounter+") - "+subject);
		this.setContent(content);
		this.setContain(subjectContain);
		this.setDoesNotContain(subjectDoesNotContain);
		this.setContentContain(contentContain);
		this.setContentDoesNotContain(contentDoesNotContain);
		this.setSensitivityHeader(sensitivityHeader);		
	}

	public void send() {
		if(header == null) {
			Mailer.send(from,to,subject,content);			
		}else {
			Mailer.send(from,to,subject,content,header);
		}
	}
	
	public boolean passed(ArrayList<Mail> mails) {
		boolean mailFound = false;

		boolean subjectContainPass = false;
		boolean subjectDoesNotContainPass = false;

		boolean contentContainPass = false;
		boolean contentDoesNotContainPass = false;
		
		
		for (Mail mailObj : mails) {
			if(mailObj.getSubject().contains(subjectID)) {
				mailFound = true;
				if(subjectContain!=null) {
					if(mailObj.getSubject().contains(subjectContain)) {
						subjectContainPass=true;
					}
				}else {
					subjectContainPass=true;
				}
				if(subjectDoesNotContain!=null) {
					if(!mailObj.getSubject().contains(subjectDoesNotContain)) {
						subjectDoesNotContainPass=true;
					}
				}else {
					subjectDoesNotContainPass=true;
				}
				if(contentContain!=null) {
					if(mailObj.getContent().contains(contentContain)) {
						contentContainPass=true;
					}
				}else {
					contentContainPass=true;
				}
				if(contentDoesNotContain!=null) {
					if(!mailObj.getContent().contains(contentDoesNotContain)) {
						contentDoesNotContainPass=true;
					}
				}else {
					contentDoesNotContainPass=true;
				}
			}
		}
		
		boolean passed = mailFound && subjectContainPass && subjectDoesNotContainPass && contentContainPass && contentDoesNotContainPass;
		String reasonFailed ="";
		if(!passed) {
			if(!mailFound) {
				reasonFailed = "could not find mail!";
			}else {
				String reasonSubjectContainFail			= subjectContainPass			?"":"NOT containing in subject *"+subjectContain+"*";
				String reasonSubjectDoesNotContainFail	= subjectDoesNotContainPass		?"":"containing in subject *"+subjectDoesNotContain+"*";
				String reasonContentContainFail 		= contentContainPass			?"":"NOT containing in content *"+contentContain+"*";
				String reasonContentDoesNotContainFail	= contentDoesNotContainPass		?"":"containing in content *"+contentDoesNotContain+"*";

				reasonFailed =	reasonSubjectContainFail+
								(reasonSubjectContainFail.equals("")?"":" and ")+
								reasonSubjectDoesNotContainFail+
								(reasonSubjectDoesNotContainFail.equals("")?"":" and ")+
								reasonContentContainFail+
								(reasonContentContainFail.equals("")?"":" and ")+
								reasonContentDoesNotContainFail;
			}
		}
		System.out.println("  |__ "+(passed?"✓":"x")+" "+testCaseName+" "+(passed?"passed":"failed due to: "+reasonFailed));
		return passed;
	}

	public MailAccount getFrom() {
		return from;
	}

	public void setFrom(MailAccount from) {
		this.from = from;
	}

	public MailAccount getTo() {
		return to;
	}

	public void setTo(MailAccount to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContain() {
		return subjectContain;
	}

	public void setContain(String contain) {
		this.subjectContain = contain;
	}

	public String getDoesNotContain() {
		return subjectDoesNotContain;
	}

	public void setDoesNotContain(String doesNotContain) {
		this.subjectDoesNotContain = doesNotContain;
	}

	public int getTcn() {
		return tcn;
	}

	public void setTcn(int tcn) {
		this.tcn = tcn;
	}

	public String getContentContain() {
		return contentContain;
	}

	public void setContentContain(String contentContain) {
		this.contentContain = contentContain;
	}

	public String getContentDoesNotContain() {
		return contentDoesNotContain;
	}

	public void setContentDoesNotContain(String contentDoesNotContain) {
		this.contentDoesNotContain = contentDoesNotContain;
	}

	public boolean isSensitivityHeader() {
		return sensitivityHeader;
	}

	public void setSensitivityHeader(boolean sensitivityHeader) {
		this.sensitivityHeader = sensitivityHeader;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
}
