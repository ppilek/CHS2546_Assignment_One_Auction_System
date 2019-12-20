package controllers;

import entries.NotificationU1264982;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javaspace.SpaceUtils;
import models.Bid;
import models.Lot;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import views.AddLotDialog;
import views.BidLotDialog;
import views.ViewLotDialog;
import views.UserProfileDialog;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AuctionBoardController implements Initializable, RemoteEventListener {

    @FXML
    private AnchorPane AuctionBoardAnchorPane;
    @FXML
    private Label label_status;
    @FXML
    public TextField notificationField;
    @FXML
    private TableView<Lot> lots_table_data;
    @FXML
    private TableColumn<Lot, String> col_index;
    @FXML
    private TableColumn<Lot, String> col_seller;
    @FXML
    private TableColumn<Lot, String> col_name;
    @FXML
    private TableColumn<Lot, String> col_description;
    @FXML
    private TableColumn<Lot, String> col_originalPrice;
    @FXML
    private TableColumn<Lot, String> col_status;
    @FXML
    private TableColumn<Lot, String> col_buyer;

    private String username = null;
    private UserController userController;
    private LotController lotController;
    private BidController bidController;
    private JavaSpace05 space;
    private TransactionManager transactionManager;
    private ObservableList<Lot> lots_data;

    public AuctionBoardController() {
        // Set up the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
            System.out.println("New Security Manager was added.");
        } else {
            System.out.println("Security Manager exist.");
        }

        // Find the transaction manager on the network
        transactionManager = SpaceUtils.getManager();
        if (transactionManager == null) {
            System.err.println("Failed to find the Transaction Manager");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to find the Transaction Manager");
            alert.showAndWait();
            System.exit(1);
        } else {
            System.out.println("Transaction Manger exist.");
        }

        // Find the Java Space on the network
        space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to find the JavaSpace");
            alert.showAndWait();
            System.exit(1);
        } else {
            System.out.println("Connected to JavaSpace");
        }

        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);
        try {
            RemoteEventListener remoteEventListener = (RemoteEventListener) myDefaultExporter.export(this);
            NotificationU1264982 notificationTemplate = new NotificationU1264982();
            space.notify(notificationTemplate, null, remoteEventListener, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (space == null) {
            setStatus(Color.TOMATO, "JavaSpace: Check connection");
        } else {
            setStatus(Color.GREEN, "JavaSpace: Connected");
        }
        lots_data = FXCollections.observableArrayList();
        lotController = new LotController(space, lots_data, lots_table_data);
        bidController = new BidController(space, notificationField);
        notificationField.setEditable(false);
        initTable();
        loadData(lots_data);
    }

    public UserController getUserController() {
        return userController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void signOutButtonAction(ActionEvent actionEvent) {
        System.out.println(getUsername());
        System.out.println(userController.readUserInfoFromSpace(getUsername()).toString());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("SING OUT and CLOSE application.");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("Ok");
            if (getUserController().singOutToSpace(getUsername()).equals("Success")) {
                Platform.exit();
                System.exit(0);
            }
        }
    }

    public void viewLotButtonAction(ActionEvent actionEvent) {
        Lot selectedLot = lots_table_data.getSelectionModel().getSelectedItem();

        if(selectedLot == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Lot Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the lot.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("View Lot");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/ViewLotDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ViewLotDialog viewLotDialog = fxmlLoader.getController();
        viewLotDialog.showSelectedLot(bidController, selectedLot);

        ButtonType buyItNow = new ButtonType("Buy it now");
        ButtonType acceptBid = new ButtonType("Accept Bid");
        ButtonType withdrawLot = new ButtonType("Withdraw");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        String userSeller = selectedLot.getUserSeller();

        if(selectedLot.getStatus().equals("pending")) {
            if( !userSeller.equals(getUsername())) {
                dialog.getDialogPane().getButtonTypes().add(buyItNow);
                Button buyItNowButton = (Button) dialog.getDialogPane().lookupButton(buyItNow);
                buyItNowButton.addEventHandler(ActionEvent.ACTION, e -> {
                    lotController.updateLot(transactionManager, Integer.parseInt(selectedLot.getIndex()), getUsername(), Double.parseDouble(selectedLot.getOriginalPrice()));
                });
            }
        }

        if(userSeller.equals(getUsername())) {
            if( !selectedLot.getStatus().equals("sold")) {
                dialog.getDialogPane().getButtonTypes().add(acceptBid);
                Button acceptBidButton = (Button) dialog.getDialogPane().lookupButton(acceptBid);
                acceptBidButton.addEventHandler(ActionEvent.ACTION, e -> {
                    TableView<Bid>  listBids = viewLotDialog.getBid_table();
                    Bid selectedBid = listBids.getSelectionModel().getSelectedItem();
                    if(selectedBid == null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("No Bid Selected");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select the bid.");
                        alert.show();
                        return;
                    }
                    lotController.acceptSelectedBid(transactionManager, selectedLot, selectedBid);
                });
            } else {
                dialog.getDialogPane().getButtonTypes().add(withdrawLot);
                Button withdrawLotButton = (Button) dialog.getDialogPane().lookupButton(withdrawLot);
                withdrawLotButton.addEventHandler(ActionEvent.ACTION, e -> {
                    // Take bids from space if is something to take
                    bidController.takeSelectedLotBidsFromSpace(Integer.parseInt(selectedLot.getIndex()));
                    // Add lot sold price to user balance
                    getUserController().addSoldLotPriceToAccount(getUsername(), Double.parseDouble(selectedLot.getSoldPrice()));
                    // Remove selected lot from space
                    lotController.removeSelectedLotFromSpace(Integer.parseInt(selectedLot.getIndex()));
                    lots_table_data.getItems().clear();
                    loadData(lots_data);
                });
            }
        }
        dialog.showAndWait();
    }

    public void bitLotButtonAction(ActionEvent actionEvent) {
        Lot selectedContact = lots_table_data.getSelectionModel().getSelectedItem();
        if(selectedContact == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Lot Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the lot you want to bit.");
            alert.showAndWait();
            return;
        }

        if(selectedContact.getStatus().equals("sold")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SOLD");
            alert.setHeaderText(null);
            alert.setContentText("This lot is sold please bid another one.");
            alert.showAndWait();
        } else {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
            dialog.setTitle("Bid Lot");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/BidLotDialog.fxml"));
            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            BidLotDialog bidLotDialog = fxmlLoader.getController();
            bidLotDialog.showSelectedLot(selectedContact);

            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, e -> {
                System.out.println("Pressed OK button from AddLotDialog");
                if(bidLotDialog.checkBidPriceRequiredFields().equals("Error")) {
                    e.consume();
                } else if(bidLotDialog.checkBidPriceRequiredFields().equals("Success")) {
                    bidLotDialog.bidSelectedLot(transactionManager, bidController, getUsername());
                }
            });
            dialog.showAndWait();
        }
    }

    public void addLotButtonAction(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("New Lot Form");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/AddLotDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        AddLotDialog addLotDialog = fxmlLoader.getController();

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            if(addLotDialog.checkRequiredFields().equals("Error")) {
                e.consume();
            } else if(addLotDialog.checkRequiredFields().equals("Success")) {
                addLotDialog.newLot(transactionManager, lotController, getUsername());
            }
        });
        dialog.showAndWait();
    }

    public void userProfileButtonAction(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("User Profile");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/UserProfileDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        UserProfileDialog userProfileDialog = fxmlLoader.getController();
        userProfileDialog.getUserProfile(getUserController(), getUsername());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {

        NotificationU1264982 notificationEntryTemplate = new NotificationU1264982();

        try {

            NotificationU1264982 notificationEntry = (NotificationU1264982) space.readIfExists(notificationEntryTemplate, null, 500);

            if(notificationEntry == null) {
                System.out.println("Notification no found.");
            } else {
                if(notificationEntry.getLotSeller().equals(getUsername())) {

                    notificationField.clear();
                    notificationField.setStyle("-fx-text-fill: #1620A1; -fx-font-size: 14px;");
                    notificationField.setText("!!! Sold !!! " + notificationEntry.getLotBuyer() + ", bought Lot: " + notificationEntry.getLotTitle() + ", for £" + notificationEntry.getLotPrice());
                    lots_data.clear();
                    loadData(lots_data);
                }

                if(notificationEntry.getBidAccepted() && notificationEntry.getLotBuyer().equals(getUsername())) {
                    notificationField.clear();
                    notificationField.setStyle("-fx-text-fill: #1620A1; -fx-font-size: 14px;");
                    notificationField.setText("!!! Sold !!! [ " + notificationEntry.getLotSeller() + "  ] accepted your bid, Lot: " + notificationEntry.getLotTitle() + ", bid: £" + notificationEntry.getLotPrice() );
                    lots_data.clear();
                    loadData(lots_data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTable() {
        initCols();
    }

    private void initCols() {
        col_index.setCellValueFactory(new PropertyValueFactory<>("index"));
        col_seller.setCellValueFactory(new PropertyValueFactory<>("userSeller"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_originalPrice.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_buyer.setCellValueFactory(new PropertyValueFactory<>("userBuyer"));
    }

    private void loadData(ObservableList<Lot> lots_data) {
        lots_table_data.setItems(lotController.loadLotsFromSpace(lots_data));
    }

    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
    }
}
