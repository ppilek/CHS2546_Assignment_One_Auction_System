package views;

import controllers.UserController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.User;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileDialog implements Initializable {

    @FXML
    private TextField indexField, usernameField, isSingInField;
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indexField.setEditable(false);
        usernameField.setEditable(false);
        passwordField.setEditable(false);
        isSingInField.setEditable(false);
    }

    public void getUserProfile(UserController userController, String username) {

        User user = userController.readUserInfoFromSpace(username);

        indexField.setText(String.valueOf(user.getIndex()));
        usernameField.setText(user.getUsername());
        passwordField.setText(user.getPassword());
        isSingInField.setText(String.valueOf(user.isSingIn()));
    }
}
