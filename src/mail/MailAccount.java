package mail;

public class MailAccount {
	private String accountName;
	private String login;
	private String pw;
	private String inboxFolderName;
	private String hostSmtp;
	private int portSmtp;
	private boolean secureSmtp;
	private String hostImap;
	private int portImap;
	private boolean secureImap;
	private boolean trustAllCerts;
	private String address;

	public MailAccount(String accountName, String address, String login, String pw, String inboxFolderName, String hostSmtp, int portSmtp, boolean secureSmtp, String hostImap, int portImap, boolean secureImap, boolean trustAllCerts) {
		this.setAccountName(accountName);
		this.setAddress(address);
		this.setLogin(login);
		this.setPw(pw);
		this.setInboxFolderName(inboxFolderName);
		this.setHostSmtp(hostSmtp);
		this.setPortSmtp(portSmtp);
		this.setSecureSmtp(secureSmtp);
		this.setHostImap(hostImap);
		this.setPortImap(portImap);
		this.setSecureImap(secureImap);
		this.setTrustAllCerts(trustAllCerts);
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getInboxFolderName() {
		return inboxFolderName;
	}

	public void setInboxFolderName(String inboxFolderName) {
		this.inboxFolderName = inboxFolderName;
	}

	public String getHostSmtp() {
		return hostSmtp;
	}

	public void setHostSmtp(String hostSmtp) {
		this.hostSmtp = hostSmtp;
	}

	public int getPortSmtp() {
		return portSmtp;
	}

	public void setPortSmtp(int portSmtp) {
		this.portSmtp = portSmtp;
	}

	public boolean isSecureSmtp() {
		return secureSmtp;
	}

	public void setSecureSmtp(boolean secureSmtp) {
		this.secureSmtp = secureSmtp;
	}

	public String getHostImap() {
		return hostImap;
	}

	public void setHostImap(String hostImap) {
		this.hostImap = hostImap;
	}

	public int getPortImap() {
		return portImap;
	}

	public void setPortImap(int portImap) {
		this.portImap = portImap;
	}

	public boolean isSecureImap() {
		return secureImap;
	}

	public void setSecureImap(boolean secureImap) {
		this.secureImap = secureImap;
	}

	public boolean isTrustAllCerts() {
		return trustAllCerts;
	}

	public void setTrustAllCerts(boolean trustAllCerts) {
		this.trustAllCerts = trustAllCerts;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String toString() {
		return getAccountName()+" "+getAddress()+" ("+getHostImap()+","+getHostSmtp()+")";
	}
}