package emm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Mail_saver_xml implements Mail_saver{
	
	private String filepath;
	private String sentpath;
	private Document doc;
	private Element root;
	private Credentials c;
	
	private Document doc2;
	private Element root2;
	
	public Mail_saver_xml(Credentials c) {
		this.c = c;
		this.filepath = Consts.XML_SAVED_PATH.concat(c.getUsername()).concat(".xml");
		if(!new File(this.filepath).exists()) {
			File file = new File(this.filepath);
			try {
				file.createNewFile();
				FileWriter writer = new FileWriter(this.filepath);
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<emails>\r\n"
						+ "</emails>");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.sentpath = Consts.XML_SENT_PATH.concat(c.getUsername()).concat(".xml");
		if(!new File(this.sentpath).exists()) {
			File file2 = new File(this.sentpath);
			try {
				file2.createNewFile();
				FileWriter writer2 = new FileWriter(this.sentpath);
				writer2.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<emails>\r\n"
						+ "</emails>");
				writer2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		load();
		load2();
	}

	private void save() {
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc, new FileOutputStream(this.filepath));
		} catch (Exception e) {
			e.printStackTrace();}
	}
	
	private void save2() {
		try {
			XMLOutputter out2 = new XMLOutputter(Format.getPrettyFormat());
			out2.output(doc2, new FileOutputStream(this.sentpath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void load() {
		try {
			SAXBuilder sxb = new SAXBuilder();
			doc = sxb.build(new File(this.filepath));
			this.root = doc.getRootElement();
		} catch (Exception e) {
			System.out.println("Erreur : " + e.getMessage());
		}
		
	}
	
	private void load2() {
		try {
			SAXBuilder sxb2 = new SAXBuilder();
			doc2 = sxb2.build(new File(this.sentpath));
			this.root2 = doc2.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public int add_mail(Email mail) {
		if(mail != null) {
			Element e = new Element("email");
			Attribute a1 = new Attribute("id", ""+mail.getId());
			Element a2 = new Element("from");
			Element a3 = new Element("to");
			Element a4 = new Element("subject");
			Element a5 = new Element("body");
			Element a6 = new Element("is_archived");
			Element a7 = new Element("is_attached");
			Element a8 = new Element("Attachements");
			Element a9 = new Element("date");
			e.setAttribute(a1);
			a2.addContent(mail.getFrom());
			a3.addContent(mail.getTo());
			a4.addContent(mail.getSubject());
			a5.addContent(mail.getBody());
			a6.addContent(""+mail.get_archived());
			a7.addContent(""+mail.get_attached_status());
			a9.addContent(mail.getDate().toString());
			if(mail.get_attached_status()) {
				for(int i=0; i < mail.getPath().size();i++) {
					Element temp = new Element("Attachement");
					Attribute atemp = new Attribute("Name",mail.getPath().get(i).getName());
					temp.addContent(mail.getPath().get(i).getPath());
					temp.setAttribute(atemp);
					a8.addContent(temp);
				}
			}
			e.addContent(a2);
			e.addContent(a3);
			e.addContent(a4);
			e.addContent(a5);
			e.addContent(a6);
			e.addContent(a7);
			e.addContent(a8);
			root.addContent(e);
			save();
			return 0;
		}else return 1;
	}

	
	public int delete_mail(Email m) {
		List<Element> l = this.root.getChildren();
		if(l != null) {
			for(Element e : l) {
				if(e.getAttributeValue("id").equals(""+m.getId())) {
					root.removeContent(e);
					save();
					return 0;
				}
			}
			return 1;
		}
		return 1;
	}
	
	public ArrayList<Email> get_Mails(){
		List<Email> list = new ArrayList<Email>();
		List<Element> l = root.getChildren();
		if(l != null) {
			for (Element e : l) {
				Email em = new Email(Integer.parseInt(e.getAttributeValue("id")), e.getChildText("from"),e.getChildText("date"),e.getChildText("to"),e.getChildText("subject"),e.getChildText("body"));
				if(Boolean.parseBoolean(e.getChildText("is_archived")))em.set_archived(true);
				if(Boolean.parseBoolean(e.getChildText("is_attached"))) {
					em.set_archived(true);
					Element temp = e.getChild("Attachements");
					List<Element> l2 = e.getChildren();
					ArrayList<Attachement> att = new ArrayList<Attachement>();
					for(Element e2 : l2) {
						Attachement a = new Attachement(e2.getAttributeValue("Name"),e2.getText());
						att.add(a);
					}
					em.setPath(att);
				}
				list.add(em);
			}
		}
		return (ArrayList<Email>) list;
	}
	
	public int add_mail_sent(Email mail) {
		if(mail != null) {
			Element e = new Element("email");
			Attribute a1 = new Attribute("id", ""+mail.getId());
			Element a2 = new Element("from");
			Element a3 = new Element("to");
			Element a4 = new Element("subject");
			Element a5 = new Element("body");
			Element a6 = new Element("is_archived");
			Element a7 = new Element("is_attached");
			Element a8 = new Element("Attachements");
			Element a9 = new Element("date");
			e.setAttribute(a1);
			a2.addContent(mail.getFrom());
			a3.addContent(mail.getTo());
			a4.addContent(mail.getSubject());
			a5.addContent(mail.getBody());
			a6.addContent(""+mail.get_archived());
			a7.addContent(""+mail.get_attached_status());
			a9.addContent(mail.getDate().toString());
			if(mail.get_attached_status()) {
				for(int i=0; i < mail.getPath().size();i++) {
					Element temp = new Element("Attachement");
					Attribute atemp = new Attribute("Name",mail.getPath().get(i).getName());
					temp.addContent(mail.getPath().get(i).getPath());
					temp.setAttribute(atemp);
					a8.addContent(temp);
				}
			}
			e.addContent(a2);
			e.addContent(a3);
			e.addContent(a4);
			e.addContent(a5);
			e.addContent(a6);
			e.addContent(a7);
			e.addContent(a8);
			root2.addContent(e);
			save2();
			return 0;
		}else return 1;
	}
	
	public int delete_mail_sent(Email m) {
		List<Element> l = this.root2.getChildren();
		if(l != null) {
			for(Element e : l) {
				if(e.getAttributeValue("id").equals(""+m.getId())) {
					root2.removeContent(e);
					save2();
					return 0;
				}
			}
			return 1;
		}
		return 1;
	}
	
	public ArrayList<Email> get_Mails_sent(){
		List<Email> list = new ArrayList<Email>();
		List<Element> l = root2.getChildren();
		if(l != null) {
			for (Element e : l) {
				Email em = new Email(Integer.parseInt(e.getAttributeValue("id")), e.getChildText("from"),e.getChildText("date"),e.getChildText("to"),e.getChildText("subject"),e.getChildText("body"));
				if(Boolean.parseBoolean(e.getChildText("is_archived")))em.set_archived(true);
				if(Boolean.parseBoolean(e.getChildText("is_attached"))) {
					em.set_archived(true);
					Element temp = e.getChild("Attachements");
					List<Element> l2 = e.getChildren();
					ArrayList<Attachement> att = new ArrayList<Attachement>();
					for(Element e2 : l2) {
						Attachement a = new Attachement(e2.getAttributeValue("Name"),e2.getText());
						att.add(a);
					}
					em.setPath(att);
				}
				list.add(em);
			}
		}
		return (ArrayList<Email>) list;
	}
}
