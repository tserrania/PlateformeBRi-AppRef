package login;

public class Prog {

	private String login;
	private String pass;
	private String ftpurl;
	
	public Prog(String login, String pass, String ftpurl) {
		this.login = login;
		this.pass = pass;
		this.ftpurl = ftpurl;
	}

	public String getLogin() {
		return login;
	}

	public String getPass() {
		return pass;
	}

	public String getFtpurl() {
		return ftpurl;
	}

	public void setFtpurl(String url) {
		ftpurl = url;
	}
	
	
}
