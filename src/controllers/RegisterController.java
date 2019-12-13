package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label label_status;
    @FXML
    private Button btnSignUp, btnSignIn;

    private UserController userController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
        userController  = new UserController(space);
        if (space == null) {
            setStatus(Color.TOMATO, "JavaSpace: Check connection");
        } else {
            setStatus(Color.GREEN, "JavaSpace: Connected");
        }
    }

    @FXML
    void signInButtonAction(MouseEvent event) {
        if(event.getSource() == btnSignIn) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/views/Login.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    @FXML
    void signUpButtonAction(MouseEvent event) {
        if (event.getSource() == btnSignUp) {
            if (signUp().equals("Success")) {
                setStatus(Color.GREEN, "Registration Successful. Please Login.");
                txtUsername.setText("");
                txtPassword.setText("");
            } else {
                setStatus(Color.TOMATO, signUp());
            }
        }
    }

    private String signUp() {
        String status = "Success";
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        if(username.isEmpty() || password.isEmpty()) {
//            setLabelError(Color.TOMATO, "Empty credentials");
            status = "Empty credentials";
        } else {
//            System.out.println(userController.singUpToSpace(username, password));
            status = userController.singUpToSpace(username, password);
//            System.out.println(status);
        }
        return status;
    }

    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
        System.out.println(text);
    }
}
