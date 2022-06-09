package emm;

import java.util.ArrayList;

public interface Mail_saver {
	public int add_mail(Email e);
	public int delete_mail(Email m);
	public ArrayList<Email> get_Mails();
}
