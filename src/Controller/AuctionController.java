package Controller;

import Model.Item;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class AuctionController implements Initializable {

    @FXML
    private Button itemButton;
    @FXML
    private TextField itemNameTextFiled, itemDescriptionTextFiled;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }

    public void getItem(ActionEvent event) {
        System.out.println("getItem Button Clicked!");

        Item item = new Item("Mobile Phone", "Sony Xperia XZ2");

        itemNameTextFiled.setText(item.getName());
        itemDescriptionTextFiled.setText(item.getDescription());
    }

    public void clearItem() {
        System.out.println("clearItem Button Clicked!");

        itemNameTextFiled.setText("");
        itemDescriptionTextFiled.setText("");
    }
}
