package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletionService;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import emm.*;

public class Inbox implements Initializable{
	private Mail_saver_xml saver;
	//private Sender sender;
	private Reciever reciever;
	private ArrayList<Email> recieved_mails;
	private ArrayList<Email> archived_mails;
	private ArrayList<Email> saved_mails;
	private ArrayList<Email> sent_mails;
	private double lastX;
	private double lastY;
	private String currentView;
	
	//inbox
	@FXML
	private VBox mailVbox;
	private String lastId;
	@FXML
	private Text userEmail;
	//mail view
	@FXML
	private Text previewFrom;
	@FXML
	private Text previewSubj;
	@FXML
	private JFXTextArea previewBody;
	@FXML
	private VBox attach;
	
	//select bar
	@FXML
	private Button normal;
	@FXML
	private Button saved;
	@FXML
	private Button archived;
	@FXML
	private Button sent;
	@FXML
	private Button logoutButton;
	
	//parameter bar
	@FXML
	private Button newMail;
	
	public static boolean repliable;
	public static String replyTo;
	public static String replySubj;
	public static boolean is_reply;
	
	public Inbox(){
		is_reply = false;
		repliable = false;
		this.currentView="normal";
		this.lastX = 24;
		this.lastY = 77;
		this.saver = new Mail_saver_xml(Main.credentials);
		//this.sender = new Sender(Main.credentials);
		this.reciever = new Reciever(Main.credentials);
		this.archived_mails = new ArrayList<Email>();
		this.saved_mails = new ArrayList<Email>();
		this.sent_mails = new ArrayList<Email>();
		this.sent_mails = saver.get_Mails_sent();
		this.saved_mails = saver.get_Mails();
		for(int i=0; i<this.saved_mails.size(); i++) {
			if(this.saved_mails.get(i).isIs_archived()) {
				this.archived_mails.add(this.saved_mails.remove(i));
			}
		}
		this.sent_mails = saver.get_Mails_sent();
		/*this.recieved_mails = new ArrayList<Email>();
		this.recieved_mails.add(email);*/
	}
	
	 public void initialize(URL location, ResourceBundle resources) {
	        init();
	        showMails("normal");
	    }
	
	public void init() {
		this.userEmail.setText(Main.credentials.getUsername());
		this.recieved_mails = new ArrayList<Email>();
		this.recieved_mails = reciever.downloadEmails();
		this.normal.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!currentView.equals("normal"))
					showMails("normal");
					currentView = "normal";
			}
		});
		this.saved.setOnMouseClicked(event -> {
			if(!this.currentView.equals("saved"))
			showMails("saved");
			this.currentView = "saved";
		});
		this.archived.setOnMouseClicked(event -> {
			if(!this.currentView.equals("archived"))
			showMails("archived");
			this.currentView = "archived";
		});
		this.sent.setOnMouseClicked(event -> {
			if(!this.currentView.equals("sent"))
			showMails("sent");
			this.currentView = "sent";
		});
	}
	
	public void showMails(String type) {
		this.mailVbox.getChildren().clear();
		switch(type) {
		case "normal":
			for(Email e:this.recieved_mails) {
				add(e,"normal");
			}
			break;
		case "archived":
			for(Email e:this.archived_mails) {
				add(e,"archived");
			}
			break;
		case "saved":
			for(Email e:this.saved_mails) {
				add(e,"saved");
			}
			break;
		case "sent":
			for(Email e:this.sent_mails) {
				add(e,"sent");
			}
			break;
		default:
			for(Email e:this.recieved_mails) {
				add(e,"normal");
			}
		}
	}
	
	public void add(Email e, String type) {
		this.mailVbox.getChildren().add(createPane(e.getSubject(),e.getFrom(),e.getId(),type));
	}
	
	private Pane createPane(String subject, String from, int id, String type) {
		double x = 14;
		double y1 = 16;
		double y2 = 33;
		Pane temp = new Pane();
		Text subj = new Text();
		Text frm = new Text();
		temp.setStyle("-fx-background-color: ffffff; -fx-background-radius: 1em;");
		temp.setPrefWidth(276);
		temp.setPrefHeight(44);
		subj.setText("From: "+from);
		frm.setText("Subject: "+subject);
		frm.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 12));
		temp.getChildren().add(frm);
		frm.setLayoutX(x);
		frm.setLayoutY(y1);
		temp.getChildren().add(subj);
		subj.setLayoutX(x);
		subj.setLayoutY(y2);
		subj.setFont(Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 12));
		temp.setLayoutX(this.lastX);
		temp.setLayoutY(this.lastY);
		temp.setId(""+id);
		this.lastY += 54;
		
		switch(type) {
		case "normal":
			temp.setOnMouseClicked(event -> {
				this.lastId = (""+id);
				display_msgs(""+id);
			});
			break;
		case "archived":
			temp.setOnMouseClicked(event -> {
				this.lastId = (""+id);
				display_archived(""+id);
			});
			break;
		case "saved":
			temp.setOnMouseClicked(event -> {
				this.lastId = (""+id);
				display_saved(""+id);
			});
			break;
		case "sent":
			temp.setOnMouseClicked(event -> {
				this.lastId = (""+id);
				display_sent(""+id);
			});
		default:
			temp.setOnMouseClicked(event -> {
				this.lastId = (""+id);
				System.out.println(temp.getId());
			});
		}
		
		
		
		return temp;
	}
	
	private void display_msgs(String id){
		this.attach.getChildren().clear();
		Email e = null;
		if(this.recieved_mails == null)return;
		for(int i=0; i<this.recieved_mails.size();i++) {
			if((this.recieved_mails.get(i).getId()+"").equals(id)) {
				e = this.recieved_mails.get(i);
				this.lastId = this.recieved_mails.get(i).getId()+"";
				break;
			}
		}
		if(e == null) return;
		this.previewFrom.setText(e.getFrom());
		this.previewSubj.setText(e.getSubject());
		this.previewBody.setText(e.getBody());
		repliable = true;
		replyTo = e.getFrom();
		replySubj = "RE: "+e.getSubject();
		
		if(e.get_attached_status()) {
			ArrayList<Attachement> al = e.getPath();
			for(Attachement a:al) {
				Hyperlink hl = new Hyperlink();
				hl.setText(a.getName());
				this.attach.getChildren().add(hl);
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
		
	}
	
	private void display_saved(String id) {
		Email e = null;
		if(this.saved_mails == null)return;
		for(int i=0; i<this.saved_mails.size();i++) {
			if((this.saved_mails.get(i).getId()+"").equals(id)) {
				e = this.saved_mails.get(i);
				this.lastId = this.saved_mails.get(i).getId()+"";
				break;
			}
		}
		if(e == null) return;
		this.previewFrom.setText(e.getFrom());
		this.previewSubj.setText(e.getSubject());
		this.previewBody.setText(e.getBody());
		repliable = true;
		replyTo = e.getFrom();
		replySubj = "RE: "+e.getSubject();
		
		if(e.get_attached_status()) {
			ArrayList<Attachement> al = e.getPath();
			for(Attachement a:al) {
				Hyperlink hl = new Hyperlink();
				hl.setText(a.getName());
				this.attach.getChildren().add(hl);
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
	}
	
	private void display_archived(String id) {
		Email e = null;
		if(this.archived_mails==null) return;
		for(int i=0; i<this.archived_mails.size();i++) {
			if((this.archived_mails.get(i).getId()+"").equals(id)) {
				e = this.archived_mails.get(i);
				this.lastId = this.archived_mails.get(i).getId()+"";
				break;
			}
		}
		if(e == null) return;
		this.previewFrom.setText(e.getFrom());
		this.previewSubj.setText(e.getSubject());
		this.previewBody.setText(e.getBody());
		repliable = true;
		replyTo = e.getFrom();
		replySubj = "RE: "+e.getSubject();
		
		if(e.get_attached_status()) {
			ArrayList<Attachement> al = e.getPath();
			for(Attachement a:al) {
				Hyperlink hl = new Hyperlink();
				hl.setText(a.getName());
				this.attach.getChildren().add(hl);
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
	}
	
	private void display_sent(String id) {
		Email e = null;
		if(this.sent_mails==null) return;
		for(int i=0; i<this.sent_mails.size();i++) {
			if((this.sent_mails.get(i).getId()+"").equals(id)) {
				e = this.sent_mails.get(i);
				this.lastId = this.sent_mails.get(i).getId()+"";
				break;
			}
		}
		if(e == null) return;
		this.previewFrom.setText(e.getFrom());
		this.previewSubj.setText(e.getSubject());
		this.previewBody.setText(e.getBody());
		repliable = true;
		replyTo = e.getFrom();
		replySubj = "RE: "+e.getSubject();
		
		if(e.get_attached_status()) {
			ArrayList<Attachement> al = e.getPath();
			for(Attachement a:al) {
				Hyperlink hl = new Hyperlink();
				hl.setText(a.getName());
				this.attach.getChildren().add(hl);
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
	}
	
	public void deleteMail() {
		if(this.lastId == null) return;
		switch(this.currentView) {
		case "normal":
				for(Email e:this.recieved_mails) {
					if((""+e.getId()).equals(this.lastId)) {
						this.recieved_mails.remove(e);
						break;
					}
					this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					this.lastId = null;
					repliable = false;
					this.attach.getChildren().clear();
					showMails("normal");
				}
				break;
		case "saved":
			for(Email e:this.saved_mails) {
				if((""+e.getId()).equals(this.lastId)) {
					saver.delete_mail(e);
					this.saved_mails.remove(e);
					break;
				}
				this.previewFrom.setText("");
				this.previewSubj.setText("");
				this.previewBody.setText("");
				this.lastId = null;
				repliable = false;
				this.attach.getChildren().clear();
				showMails("saved");
			}
			break;
		case "archived":
			for(Email e:this.archived_mails) {
				if((""+e.getId()).equals(this.lastId)) {
					saver.delete_mail(e);
					this.archived_mails.remove(e);
					break;
				}
				this.previewFrom.setText("");
				this.previewSubj.setText("");
				this.previewBody.setText("");
				this.lastId = null;
				repliable = false;
				this.attach.getChildren().clear();
				showMails("archived");
			}
			break;
		case "sent":
			for(Email e:this.sent_mails) {
				if((""+e.getId()).equals(this.lastId)) {
					saver.delete_mail_sent(e);
					this.sent_mails.remove(e);
					break;
				}
				this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					repliable = false;
					this.attach.getChildren().clear();
				showMails("sent");
			}
			break;
		}
	}
	
	public void saveMail() {
		if(this.lastId == null) return;
		if(this.currentView.equals("normal")) {
			for(Email e:this.recieved_mails) {
				if((""+e.getId()).equals(this.lastId)) {
					this.saved_mails.add(e);
					saver.add_mail(e);
					this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					repliable = false;
					this.attach.getChildren().clear();
					this.lastId = null;
					return;
				}
			}
		}
	}
	
	public void archiveMail() {
		if(this.lastId == null) return;
		if(!this.currentView.equals("archived")) {
			switch(this.currentView) {
			case "normal":
				for(int i=0;i<this.recieved_mails.size();i++) {
					if((this.recieved_mails.get(i).getId()+"").equals(this.lastId)) {
						this.recieved_mails.get(i).set_archived(true);
						this.archived_mails.add(this.recieved_mails.remove(i));
						break;
					}
					this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					this.lastId = null;
					repliable = false;
					this.attach.getChildren().clear();
					showMails("normal");
				}
				break;
			
			case "saved":
				for(int i=0;i<this.saved_mails.size();i++) {
					if((this.saved_mails.get(i).getId()+"").equals(this.lastId)) {
						this.saved_mails.get(i).set_archived(true);
						saver.delete_mail(this.saved_mails.get(i));
						saver.add_mail(this.saved_mails.get(i));
						this.archived_mails.add(this.recieved_mails.remove(i));
						break;
					}
					this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					repliable = false;
					this.lastId = null;
					this.attach.getChildren().clear();
					showMails("saved");
				}
				break;
			case "sent":
				for(int i=0;i<this.sent_mails.size();i++) {
					if((this.sent_mails.get(i).getId()+"").equals(this.lastId)) {
						this.sent_mails.get(i).set_archived(true);
						saver.delete_mail_sent(this.sent_mails.get(i));
						saver.add_mail_sent(this.sent_mails.get(i));
						this.archived_mails.add(this.sent_mails.remove(i));
						break;
					}
					this.previewFrom.setText("");
					this.previewSubj.setText("");
					this.previewBody.setText("");
					repliable = false;
					this.lastId = null;
					this.attach.getChildren().clear();
					showMails("sent");
				}
				break;
			}
		}
	}
	public void reply() {
		if(!repliable) return;
		System.out.println("repliable");
		is_reply = true;
		newMessage();
	}
	
	public void newMessage() {
		try {
	        FXMLLoader fxmlLoader = new FXMLLoader();
	        fxmlLoader.setLocation(getClass().getResource("newMail.fxml"));
	        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
	        Stage stage = new Stage();
	        stage.setTitle("New email");
	        stage.setScene(scene);
	        stage.setResizable(false);
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void logout() {
		Main m = new Main();
		Main.credentials = null;
		try {
			m.changeSceen("login.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
