package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

import emm.Credentials;

public class Login {
	@FXML
	private TextField email;
	@FXML
	private PasswordField password;
	@FXML
	private Button login;
	@FXML
	private Text wronglog;
	
	public Login() {
	}
	
	public void userLogin() throws IOException {
		checkLogin();
	}
	
	private void checkLogin() throws IOException {
		Main m = new Main();
		Main.credentials = new Credentials(email.getText(),password.getText());
		if(Main.credentials.auth()) {
			m.changeSceen("inbox.fxml");
		}else {
			password.setText("");
			wronglog.setText("Email or password incorrect!");
		}
	}
}
