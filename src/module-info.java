module emmGUI {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.mail;
	requires java.desktop;
	requires activation;
	requires org.jdom2;
	requires javafx.graphics;
	requires com.jfoenix;
	
	opens application to javafx.graphics, javafx.fxml;
	opens emm to java.mail, java.desktop, activation, org.jdom2;
}
