package views;

import controllers.LotController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import net.jini.core.transaction.server.TransactionManager;
import java.util.regex.Pattern;

public class AddLotDialog {

    @FXML
    private TextField nameField, priceField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Label label_status;


    public void newLot(TransactionManager transactionManager, LotController lotController, String username) {

        String name = nameField.getText();
        String description = descriptionField.getText();
        double price = Double.parseDouble(priceField.getText());

        lotController.addLot(transactionManager, username, name, description, price);
    }

    public String checkRequiredFields() {
        String status = "";
        String name = nameField.getText();
        String description = descriptionField.getText();
        String price = priceField.getText();
        if(name.isEmpty() || description.isEmpty() || priceField.getText().isEmpty()) {
            setStatus(Color.TOMATO, "Empty credentials");
            status = "Error";
        } else {
            String decimalPattern = "([0-9]*)\\.([0-9]*)";
            boolean match = Pattern.matches(decimalPattern, price);
            if(match) {
                status = "Success";
            } else {
                setStatus(Color.TOMATO, "Wrong pattern price! (ex.:12.35)");
                status = "Error";
            }
        }
        return status;
    }

    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
    }
}
