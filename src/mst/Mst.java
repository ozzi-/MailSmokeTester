package mst;
import java.io.IOException;
import java.util.ArrayList;

import javax.mail.MessagingException;

import mail.Mailer;
import mail.MailAccount;
import mail.Mail;
import util.Helper;
import util.JSON;
import util.Settings;

public class Mst {

	public static void main(String[] args) throws MessagingException, IOException {

		if (args.length < 2) {
			System.err.println("Command line arguments missing");
			System.err.println("{/path/to/config.json} {/path/to/tests.json}");
			System.exit(1);
		}

		ArrayList<MailAccount> mal = JSON.loadConfig(args[0]);
		ArrayList<TestCase>    tcl = JSON.loadTestCases(args[1], mal);

		/** Clearing all Inboxes **/
		System.out.println("\r\nClearing Inboxes");
		for (MailAccount mailAccount : mal) {
			System.out.println(Settings.indentMarker+mailAccount.getAccountName());
			Mailer.clearFolderIMAP(mailAccount);
		}

		/** Sending Mails **/
		System.out.println("\r\nSending Mails");
		System.out.print(Settings.indentMarker);
		for (TestCase testCase : tcl) {
			testCase.send();
		}
		System.out.println("");
		Helper.sleep(Settings.sleepBeforeGetMails);

		/** Receive **/
		System.out.println("\r\nReceiving Mails");
		ArrayList<Mail> mailsReceived = new ArrayList<Mail>();
		for (MailAccount mailAccount : mal) {
			mailsReceived.addAll(Mailer.getMessages(mailAccount));
		}

		/** Results **/
		System.out.println("\r\nChecking Testcases");
		for (TestCase testCase : tcl) {
			testCase.passed(mailsReceived);
		}
	}
}
