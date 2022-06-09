package emm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Mailing_list_writer {
	private String filepath;
	private String sentpath;
	private Document doc;
	private Element root;
	private Credentials c;
	
	public Mailing_list_writer(Credentials c) {
		this.c = c;
		this.filepath = Consts.XML_LISTS_PATH.concat(c.getUsername()).concat(".xml");
		if(!new File(this.filepath).exists()) {
			File file = new File(this.filepath);
			try {
				file.createNewFile();
				FileWriter writer = new FileWriter(this.filepath);
				writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<Mailing_Lists>\r\n"
						+ "</Mailing_Lists>");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		load();
	}
	private void save() {
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(doc, new FileOutputStream(this.filepath));
		} catch (Exception e) {
			e.printStackTrace();}
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
	
	public int add_list(Mailing_list list) {
		if(list != null) {
			Element e = new Element("Mailing_list");
			Attribute a1 = new Attribute("name", ""+list.getName());
			e.setAttribute(a1);
			for(String em:list.getEmails()) {
				Element a = new Element("Email");
				a.addContent(em);
				e.addContent(a);
			}
			root.addContent(e);
			save();
			return 0;
		}else return 1;
	}
	
	public Mailing_list get_Mails(String name){
		ArrayList<String> list = new ArrayList<String>();
		List<Element> l = root.getChildren();
		List<Element> l2 = null;
		if(l != null) {
			for (Element e : l) {
					if(e.getAttributeValue("name").equals(name)) {
						l2 = e.getChildren();
						for(Element e2:l2) {
							list.add(e2.getText());
						}
						break;
					}
				}
			}
		return new Mailing_list(name,list);
	}
	
	public ArrayList<Mailing_list> get_All(){
		ArrayList<Mailing_list> list = new ArrayList<Mailing_list>();
		List<Element> l = root.getChildren();
		if(l != null) {
			for(Element e: l) {
				Mailing_list temp = new Mailing_list();
				ArrayList<String> t2 = new ArrayList<String>();
				temp.setName(e.getAttributeValue("name"));
				for(Element e2:e.getChildren()) {
					t2.add(e2.getText());
				}
				temp.setEmails(t2);
				list.add(temp);
			}
		}
		return list;
	}
}
