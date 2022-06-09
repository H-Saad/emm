package emm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

 
public class Reciever {
	
	private String dir;
	private Credentials c;
	
	public Reciever(Credentials c) {
		this.c = c;
		this.dir = Consts.ATTACH_SAVE_DIR.concat(c.getUsername()).concat("/");
		if(!new File(this.dir).exists()) {
			System.out.println("creating dir");
			new File(this.dir).mkdir();
		}
	}
 
    private Properties getServerProperties(String protocol, String host,
            String port) {
        Properties properties = new Properties();
 
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);
 
        // SSL setting
        properties.setProperty(
                String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(
                String.format("mail.%s.socketFactory.fallback", protocol),
                "false");
        properties.setProperty(
                String.format("mail.%s.socketFactory.port", protocol),
                String.valueOf(port));
 
        return properties;
    }
 
    public ArrayList<Email> downloadEmails() {
    	String protocol = "imap";
        Properties properties = getServerProperties(protocol, Consts.IMAP_HOST, Consts.IMAP_PORT);
        Session session = Session.getDefaultInstance(properties);
        ArrayList<Email> list = new ArrayList<Email>();
 
        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(c.getUsername(), c.getPassword());
 
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
 
            // fetches new messages from server
            int msgs_num = folderInbox.getMessageCount();
            Message[] messages = folderInbox.getMessages(msgs_num-10, msgs_num);
 
            for (int i = 0; i < 10; i++) {
            	Boolean attached = false;
            	Message message = messages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
                String subject = message.getSubject();
                Date sentDate = message.getSentDate();
                String contentType = message.getContentType();
                String messageContent = "";
 
                // store attachment file name, separated by comma
                ArrayList<Attachement> attachFiles = new ArrayList<Attachement>();
 
                if (contentType.contains("multipart")) {
                    // content may contain attachments
                    Multipart multiPart = (Multipart) message.getContent();
                    int numberOfParts = multiPart.getCount();
                    for (int partCount = 0; partCount < numberOfParts-1; partCount++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            // this part is attachment
                        	attached = true;
                        	Attachement a = new Attachement(dir +part.getFileName(),part.getFileName());
                        	part.saveFile(a.getPath());
                            attachFiles.add(a);
                        } else {
                            // this part may be the message content
                            messageContent = part.getContent().toString();
                        }
                    }
                } 	if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {
                    Object content = message.getContent();
                    if (content != null) {
                        messageContent = content.toString();
                    }
                }
                
                //regex
                
                Matcher m = Pattern.compile("(^[^<]*<)(?:.*)(>.*)").matcher(from);
                if(m.find()) {
                	from = from.replace(m.group(1), "");
                	from = from.replace(m.group(2), "");
                }
                messageContent = messageContent.replaceAll("\\s\\s+", "");//remove whitespaces
                messageContent = messageContent.replaceAll("/(\\r\\n)+|\\r+|\\n+|\\t+/","");//remove newlines
                messageContent = messageContent.replaceAll("<style.*?<\\/style>", "");//remove style tags
                messageContent = messageContent.replaceAll("<head>.*?<\\/head>", "");//remove head tags
                messageContent = messageContent.replaceAll("<.*?>", "");//remove all the other tagss
                
                System.out.println(messageContent);
                
                Email mail = new Email(i+1, from, c.getUsername(), sentDate.toString(), subject, messageContent);
                if(attached) {
                	mail.set_attached(true);
                	mail.setPath(attachFiles);
                }
                list.add(mail);
            }

 
            // disconnect
            folderInbox.close(false);
            store.close();
            return list;
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for imap.");
            ex.printStackTrace();
            return null;
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private String parseAddresses(Address[] address) {
        String listAddress = "";
 
        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }
 
        return listAddress;
    }
}
