package emm;

import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Sender{
	
	private Credentials c;
	
	public Sender(Credentials c) {
		this.c = c;
	}
	
    public boolean sendmail(String to, String subj, String body, ArrayList<Attachement> att) {
    	try {
            Message message = new MimeMessage(c.getSession());
            message.setFrom(new InternetAddress(c.getUsername()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subj);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            
            if(att != null) {
            	for(int i=0; i<att.size();i++) {
            		messageBodyPart = new MimeBodyPart();
                    String filename = att.get(i).getPath();
                    DataSource source = new FileDataSource(filename);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(att.get(i).getName());
                    multipart.addBodyPart(messageBodyPart);
            	}
            }
            message.setContent(multipart);
            Transport.send(message);

            System.out.println("Done");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}