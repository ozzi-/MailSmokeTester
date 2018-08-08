package util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import mail.MailAccount;
import mst.TestCase;

public class JSON {
		

	public static ArrayList<TestCase> loadTestCases(String path, ArrayList<MailAccount> mal) {
		String json ="";
		try {
			json = Helper.readFile(path);
		} catch (IOException e) {
			System.err.println("Cannot load testcases json file!");
			System.err.println(e.toString());
			System.exit(1);
		}		
		ArrayList<TestCase> tcl = new ArrayList<TestCase>();
		final JSONObject obj;
		String curTC = "null";
		System.out.println("\r\nLoading Testcases");
		try {
			obj = new JSONObject(json);
			String[] testCaseNames = JSONObject.getNames(obj);
			for (String testCaseName : testCaseNames) {
				curTC = testCaseName;
				JSONObject testCaseJSON = (JSONObject) obj.get(testCaseName);
				String fromJ	= testCaseJSON.getString("from");
				String toJ		= testCaseJSON.getString("to");
				MailAccount from = getMailAccountByName(mal, fromJ);
				MailAccount to	 = getMailAccountByName(mal, toJ);
				String subject	= exists(testCaseJSON, "subject")?testCaseJSON.getString("subject"):"Subject";
				String content	= exists(testCaseJSON, "content")?testCaseJSON.getString("content"):"Content";
				String subjectContain		= exists(testCaseJSON, "subject_contain")?testCaseJSON.getString("subject_contain"):null;
				String subjectNotContain	= exists(testCaseJSON, "subject_not_contain")?testCaseJSON.getString("subject_not_contain"):null;
				String contentContain		= exists(testCaseJSON, "content_contain")?testCaseJSON.getString("content_contain"):null;
				String contentNotContain	= exists(testCaseJSON, "content_not_contain")?testCaseJSON.getString("content_not_contain"):null;
				TestCase tc = new TestCase(testCaseName, from, to, subject, content, subjectContain, subjectNotContain, contentContain, contentNotContain);
				if(exists(testCaseJSON, "header")) {
					tc.setHeader(testCaseJSON.getString("header"));
				}
				tcl.add(tc);
				System.out.println("  |_ "+testCaseName);
			}
		} catch (Exception e) {
			System.err.println("Error parsing testcases file at object \""+curTC+"\"");
			System.err.println(e.toString());
			System.exit(2);
		}
	    return tcl;
	}


	private static MailAccount getMailAccountByName(ArrayList<MailAccount> mal, String name) {
		for (MailAccount ma : mal) {
			if(ma.getAccountName().equals(name)) {
				return ma;
			}
		}
		System.err.println("Could not find Mail Account - "+name);
		System.exit(3);
		return null;
	}

	
	public static ArrayList<MailAccount> loadConfig(String path) {
		String json = "";
		try {
			json = Helper.readFile(path);
		} catch (IOException e) {
			System.err.println("Cannot load config json file!");
			System.err.println(e.toString());
			System.exit(1);
		}
		ArrayList<MailAccount> mal = new ArrayList<MailAccount>();
		final JSONObject obj;
		String curItem = "null";
		System.out.println("Loading Mail Accounts");
		try {
			obj = new JSONObject(json);
			String[] items = JSONObject.getNames(obj);
			for (String item : items) {
				curItem = item;
				// TODO check for duplicate mail account names!
				if(item.equals("settings")) {
					parseSettings(obj);
				}else {
					JSONObject mailAccountJSON = (JSONObject) obj.get(item);
					String address = mailAccountJSON.getString("address");
					String login = mailAccountJSON.getString("login");
					String pw = mailAccountJSON.getString("pw");
					String inboxFolderName = mailAccountJSON.getString("inbox_folder_name");
					String hostSmtp;
					String hostImap;
					if(exists(mailAccountJSON, "host")) {
						String host = mailAccountJSON.getString("host");
						hostSmtp = host;
						hostImap = host;
					}else {
						hostSmtp = mailAccountJSON.getString("host_smtp");
						hostImap = mailAccountJSON.getString("host_imap");
					}
					int portSmtp = mailAccountJSON.getInt("port_smtp");
					int portImap = mailAccountJSON.getInt("port_imap");
					boolean secureSmtp;
					boolean secureImap;
					if(exists(mailAccountJSON, "secure")) {
						boolean secure = mailAccountJSON.getBoolean("secure");
						secureSmtp = secure;
						secureImap = secure;
					}else {
						secureSmtp = mailAccountJSON.getBoolean("secure_smtp");
						secureImap = mailAccountJSON.getBoolean("secure_imap");						
					}

					boolean trustAllCerts = exists(mailAccountJSON, "trust_all_certs")?mailAccountJSON.getBoolean("trust_all_certs"):false;
					mal.add(new MailAccount(item, address, login, pw, inboxFolderName, hostSmtp, portSmtp, secureSmtp, hostImap, portImap, secureImap, trustAllCerts));
					System.out.println("  |_ "+item+" ("+address+")");
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing config json file at object \""+curItem+"\"");
			System.err.println(e.toString());
			System.exit(2);
		}
	    return mal;
	}
	
	private static boolean exists(JSONObject obj, String key) {
		String[] items = JSONObject.getNames(obj);
		for (String item : items) {
			if(item.equals(key)) {
				return true;
			}
		}
		return false;
	}

	private static void parseSettings(final JSONObject obj) {
		JSONObject settings = (JSONObject) obj.get("settings");
		if(exists(settings, "sleep_before_get_mails")) {
			Settings.sleepBeforeGetMails = settings.getInt("sleep_before_get_mails");			
		}
		if(exists(settings, "debug_protocols")) {
			Settings.debugProtocols = settings.getBoolean("debug_protocols");
		}
	}
	
	static void parseConfig(Map<String, String> config, final JSONObject obj, String name) {
		try {
			String value = String.valueOf(obj.get(name));
			config.put(name,  value);			
		}catch(Exception e) {
			System.err.println("Cannot parse "+name+" from config json file.   ("+e.getMessage()+")");
			System.exit(1);			
		}
	}


}
