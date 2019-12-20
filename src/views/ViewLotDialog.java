package views;

import controllers.BidController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Bid;
import models.Lot;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewLotDialog implements Initializable  {

    @FXML
    private TextField indexField, sellerField, titleField, originalPriceField, soldPriceField, statusField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TableView<Bid> bid_table;
    @FXML
    private TableColumn<Bid, String> col_bid;
    @FXML
    private TableColumn<Bid, String> col_user;

    private ObservableList<Bid> bits_data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indexField.setEditable(false);
        sellerField.setEditable(false);
        titleField.setEditable(false);
        descriptionField.setEditable(false);
        originalPriceField.setEditable(false);
        soldPriceField.setEditable(false);
        statusField.setEditable(false);

        bits_data = FXCollections.observableArrayList();
        initTable();
    }

    private void initTable() {
        initCols();
    }

    private void initCols() {
        col_bid.setCellValueFactory(new PropertyValueFactory<>("bidValue"));
        col_user.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    private void loadData(BidController bitController, ObservableList<Bid> bits_data, int lotId) {
        bid_table.setItems(bitController.loadBidsFromSpace(bits_data, lotId));
    }

    public void showSelectedLot(BidController bitController, Lot lot) {
        indexField.setText(lot.getIndex());
        sellerField.setText(lot.getUserSeller());
        titleField.setText(lot.getTitle());
        descriptionField.setText(lot.getDescription());
        originalPriceField.setText(lot.getOriginalPrice());
        soldPriceField.setText(lot.getSoldPrice());
        statusField.setText(lot.getStatus());

        loadData(bitController, bits_data, Integer.parseInt(lot.getIndex()));
    }

    public TableView<Bid> getBid_table() {
        return bid_table;
    }

}
