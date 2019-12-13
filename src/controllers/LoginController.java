package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    double x, y = 0;

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label label_status;
    @FXML
    private Button btnSignIn, btnSignUp;

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
        if (event.getSource() == btnSignIn) {
            //sign in here
            if (signIn().equals("Success")) {
                try {
                    Node node = (Node) event.getSource();
                    Stage stage = (Stage) node.getScene().getWindow();
                    stage.close();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AuctionBoard.fxml"));
                    Parent root = loader.load();

                    Stage stageOne = new Stage(StageStyle.TRANSPARENT);
                    stageOne.getIcons().add(new Image("images/icon.png"));
                    //grab your root
                    root.setOnMousePressed(e -> {
                        x = e.getSceneX();
                        y = e.getSceneY();
                    });

                    //move around
                    root.setOnMouseDragged(e -> {
                        stageOne.setX(e.getScreenX() - x);
                        stageOne.setY(e.getScreenY() - y);
                    });

                    AuctionBoardController auctionBoardController = loader.getController();
                    auctionBoardController.setUsername(txtUsername.getText());
                    auctionBoardController.setUserController(userController);

                    stageOne.setScene(new Scene(root));
                    stageOne.show();

                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            } else {
                setStatus(Color.TOMATO, signIn());
            }
        }
    }

    @FXML
    void signUpButtonAction(MouseEvent event) {
        if(event.getSource() == btnSignUp) {
            try {
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();
                Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/views/Register.fxml")));
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private String signIn() {
        String status = "Success";
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        if(username.isEmpty() || password.isEmpty()) {
//            setLabelError(Color.TOMATO, "Empty credentials");
            status = "Empty credentials";
        } else {
//            System.out.println(userController.singIn(username, password));
            status = userController.singInToSpace(username, password);
        }

        return status;
    }


    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
        System.out.println(text);
    }
}
