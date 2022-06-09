package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionService;

import emm.Attachement;
import emm.Email;
import emm.Mail_saver_xml;
import emm.Mailing_list;
import emm.Mailing_list_writer;
import emm.Sender;

public class NewMail implements Initializable{
	
	private TextField toField;
	private TextField subjField;
	@FXML
	private Text errmsg;
	@FXML
	private AnchorPane pls;
	@FXML
	private TextArea bodyField;
	@FXML
	private VBox attachVbox;
	@FXML
	private Button send;
	
	private ArrayList<Attachement> att;
	private Sender sender;
	private Mailing_list_writer wr;
	private ArrayList<Mailing_list> ml;
	private Mailing_list active;
	private Mail_saver_xml msv;
	
	@FXML
	private TextField addname;
	@FXML
	private TextArea addmails;
	@FXML
	private Button createList;
	@FXML
	private Button sendtolist;
	@FXML
	private VBox mailList;
	
	public NewMail() {
		wr = new Mailing_list_writer(Main.credentials);
		this.att = null;
		this.sender = new Sender(Main.credentials);
		this.ml = wr.get_All();
		this.msv = new Mail_saver_xml(Main.credentials);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		this.pls.getChildren().add(this.toField);
		this.pls.getChildren().add(this.subjField);
		makeList();
	}
	
	private void makeList() {
		this.mailList.getChildren().clear();
		for(Mailing_list m : this.ml) {
			this.mailList.getChildren().add(this.make_btn(m.getName()));
		}
	}
	
	private void init() {
		this.toField = new TextField();
		this.toField.setLayoutX(44);
		this.toField.setLayoutY(50);
		this.toField.setPrefHeight(25);
		this.toField.setPrefWidth(290);
		this.toField.setPromptText("To:");
		
		this.subjField = new TextField();
		this.subjField.setLayoutX(44);
		this.subjField.setLayoutY(99);
		this.subjField.setPrefHeight(25);
		this.subjField.setPrefWidth(290);
		this.subjField.setPromptText("Subject:");
		
		if(Inbox.is_reply) {
			this.toField.setText(Inbox.replyTo);
			this.subjField.setText(Inbox.replySubj);
		}
	}
	
	public void sendMail() {
		this.errmsg.setText(" ");
		if(sender.sendmail(this.toField.getText(), this.subjField.getText(), this.bodyField.getText(), this.att)) {
			this.errmsg.setFill(Color.GREEN);
			this.errmsg.setText("Message sent!");
			Email e = new Email();
			e.setFrom(Main.credentials.getUsername());
			e.setTo(this.toField.getText());
			e.setSubject(this.subjField.getText());
			e.setBody(this.bodyField.getText());
			e.setPath(att);
			this.msv.add_mail_sent(e);
			
		}else {
			this.errmsg.setFill(Color.RED);
			this.errmsg.setText("Error sending msg!");
		}
	}
	
	public void add_attachement() {
		FileChooser fc = new FileChooser();
		List<File> f = fc.showOpenMultipleDialog(null);
		if(f != null) {
			this.att = new ArrayList<Attachement>();
			for(File file:f) {
				System.out.println(file.getName());
				System.out.println(file.getAbsolutePath());
				this.att.add(new Attachement(file.getAbsolutePath(),file.getName()));
			}
		}
		for(Attachement a:att) {
			Hyperlink hl = new Hyperlink();
			hl.setText(a.getName());
			this.attachVbox.getChildren().add(hl);
			hl.setOnAction((ActionEvent ev) -> {  
                File linked = new File(a.getPath()); 
                        try {
                            if(Desktop.isDesktopSupported()){
                                try {
                                    Desktop.getDesktop().open(linked);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
        }); 
		}
	}
	public Button make_btn(String name) {
		Button temp = new Button();
		temp.setText(name);
		temp.setPrefHeight(25);
		temp.setPrefWidth(141);
		temp.setStyle("-fx-background-color: none;");
		temp.setOnMouseClicked(event ->{
			if(this.ml != null) {
				for(Mailing_list m : ml) {
					if(m.getName().equals(temp.getText())) {
						active = m;
						this.addname.setText(m.getName());
						ArrayList<String> a = m.getEmails();
						String s = "";
						for(String ss:a) {
							s = s+ss+",";
						}
						s = s.substring(0, s.length() - 1);
						this.addmails.setText(s);
						break;
					}
				}
			}
		});
		return temp;
	}
	
	public void sendtolist() {
		if(this.active != null) {
			for(String m:this.active.getEmails()) {
				if(!(sender.sendmail(m, this.subjField.getText(), this.bodyField.getText(), this.att))) {
					this.errmsg.setFill(Color.RED);
					this.errmsg.setText("Error sending msg!");
					return;
				}
				Email e = new Email();
				e.setFrom(Main.credentials.getUsername());
				e.setTo(m);
				e.setSubject(this.subjField.getText());
				e.setBody(this.bodyField.getText());
				e.setPath(att);
				this.msv.add_mail_sent(e);
			}
			this.errmsg.setFill(Color.GREEN);
			this.errmsg.setText("Message sent!");
		}
	}
	
	public void add_list() {
		if(this.addname == null || this.addname.getText().equals("") || this.addmails == null || this.addmails.getText().equals("")) return;
		Mailing_list temp = new Mailing_list();
		temp.setName(this.addname.getText());
		String y = this.addmails.getText();
		List<String> t = Arrays.asList(y.split(","));
		ArrayList<String> tt = new ArrayList<String>();
		for(String s:t) {
			tt.add(s);
		}
		temp.setEmails(tt);
		this.ml.add(temp);
		this.wr.add_list(temp);
		this.makeList();
		this.addname.setText("");
		this.addmails.setText("");
	}
}
