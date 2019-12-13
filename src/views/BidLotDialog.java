package views;

import controllers.BidController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import models.Lot;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class BidLotDialog implements Initializable {

    @FXML
    private TextField indexField, sellerField, titleField, priceField, bitField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Label label_status;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indexField.setEditable(false);
        sellerField.setEditable(false);
        titleField.setEditable(false);
        descriptionField.setEditable(false);
        priceField.setEditable(false);
    }

    public void showSelectedLot(Lot lot) {
        indexField.setText(lot.getIndex());
        sellerField.setText(lot.getUserSeller());
        titleField.setText(lot.getTitle());
        descriptionField.setText(lot.getDescription());
        priceField.setText(lot.getPrice());
    }

    public void bidSelectedLot(BidController bidController, String username) {
        int lotIndex = Integer.parseInt(indexField.getText());
        double bidPrice = Double.parseDouble(bitField.getText());

        bidController.bidLot(lotIndex, bidPrice, username);
    }

    public String checkBidPriceRequiredFields() {
        String status = "";
        String bidPrice = bitField.getText();

        if(bitField.getText().isEmpty()) {
            setStatus(Color.TOMATO, "Empty bid value");
            status = "Error";
        } else {
            String decimalPattern = "([0-9]*)\\.([0-9]*)";
            boolean match = Pattern.matches(decimalPattern, bidPrice);
            if(match) {
                status = "Success";
            } else {
                setStatus(Color.TOMATO, "Wrong price! (ex. 12.35)");
                status = "Error";
            }
        }
        return status;
    }

    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
        System.out.println(text);
    }

}
