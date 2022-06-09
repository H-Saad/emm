package emm;

import java.util.ArrayList;

public class Mailing_list {
	
	private String name;
	private ArrayList<String> emails;
	
	public Mailing_list() {
		this.emails = new ArrayList<String>();
		this.name = "";
	}
	public Mailing_list(String name, ArrayList<String> emails) {
		this();
		this.name = name;
		this.emails = emails;
	}
	
	public void add_mail(String e) {
		this.emails.add(e);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getEmails() {
		return emails;
	}

	public void setEmails(ArrayList<String> emails) {
		this.emails = emails;
	}
}
