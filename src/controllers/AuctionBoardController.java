package controllers;

import entries.BuyNowEntry;
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
import models.Lot;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
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
    private TableColumn<Lot, String> col_price;
    @FXML
    private Button btn_accept, btn_reject;

    private String username = null;
    private UserController userController;
    private LotController lotController;
    private BidController bidController;
    private BuyNowController saleController;
    private JavaSpace05 space;
    private TransactionManager transactionManager;
    private RemoteEventListener theStub;

    public AuctionBoardController() {
        space = (JavaSpace05) SpaceUtils.getSpace();

        // set up the security manager
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());

        // Find the transaction manager on the network
        transactionManager = SpaceUtils.getManager();
        if (transactionManager == null) {
            System.err.println("Failed to find the transaction manager");
        }

        // create the exporter
        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            BuyNowEntry template = new BuyNowEntry(false);
            space.notify(template, null, this.theStub, Lease.FOREVER, null);

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

        notificationField.setEditable(false);

        notificationField.setVisible(false);
        btn_accept.setVisible(false);
        btn_reject.setVisible(false);


        ObservableList<Lot> lots_data = FXCollections.observableArrayList();
        lotController = new LotController(space, lots_data);
        bidController = new BidController(space);
        saleController = new BuyNowController(space);
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

    // Done
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
        } else {
            System.out.println("Cancel");
        }
    }

    // ToDo buy know
    public void viewLotButtonAction(ActionEvent actionEvent) {
        Lot selectedContact = lots_table_data.getSelectionModel().getSelectedItem();
        if(selectedContact == null) {
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
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        ButtonType buyItNow = new ButtonType("Buy it now");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(buyItNow);

        ViewLotDialog viewLotDialog = fxmlLoader.getController();
        viewLotDialog.showSelectedLot(bidController, selectedContact);

        Button buyItNowButton = (Button) dialog.getDialogPane().lookupButton(buyItNow);
        buyItNowButton.addEventHandler(ActionEvent.ACTION, e -> {
            System.out.println("Pressed [Buy it now] button from ViewLotDialog");
            saleController.addBuyNowToSpace(transactionManager, Integer.parseInt(selectedContact.getIndex()), getUsername());

        });

        dialog.showAndWait();
    }

    // Done
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

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("Bid Lot");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/BidLotDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
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
                bidLotDialog.bidSelectedLot(bidController, getUsername());
            }
        });

        dialog.showAndWait();
    }

    // Done Add lot with validation fields
    public void addLotButtonAction(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("New Lot Form");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/AddLotDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        AddLotDialog addLotDialog = fxmlLoader.getController();

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, e -> {
            System.out.println("Pressed OK button from AddLotDialog");
            if(addLotDialog.checkRequiredFields().equals("Error")) {
                e.consume();
            } else if(addLotDialog.checkRequiredFields().equals("Success")) {
                addLotDialog.newLot(transactionManager, lotController, getUsername());
            }
        });

        dialog.showAndWait();
    }

    // Done display user info
    public void userProfileButtonAction(ActionEvent actionEvent) {
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(AuctionBoardAnchorPane.getScene().getWindow());
        dialog.setTitle("User Profile");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/UserProfileDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        UserProfileDialog userProfileDialog = fxmlLoader.getController();
        userProfileDialog.getUserProfile(getUserController(), getUsername());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.showAndWait();
    }

    // Done
    private void initTable() {
        initCols();
    }

    // Done
    private void initCols() {
        col_index.setCellValueFactory(new PropertyValueFactory<>("index"));
        col_seller.setCellValueFactory(new PropertyValueFactory<>("userSeller"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_price.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    // Done
    private void loadData(ObservableList<Lot> lots_data) {
        lots_table_data.setItems(lotController.loadLotsFromSpace(lots_data));
    }

    // Done
    private void setStatus(Color color, String text) {
        label_status.setTextFill(color);
        label_status.setText(text);
        System.out.println(text);
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        BuyNowEntry buyNowEntryTemplate = new BuyNowEntry(false);

        notificationField.setVisible(false);
        btn_accept.setVisible(false);
        btn_reject.setVisible(false);

        try {

            BuyNowEntry buyNowEntry = (BuyNowEntry) space.readIfExists(buyNowEntryTemplate, null, 3000);

            if(buyNowEntry == null) {
                System.out.println("notify: Error - No object found in space");
            } else {
                System.out.println("notify: Object founded in space");

                String sellerUserName = buyNowEntry.getSellerUserName();

                if(sellerUserName.equals(getUsername())) {
                    System.out.println("notify: for me");

                    notificationField.setVisible(true);
                    btn_accept.setVisible(true);
                    btn_reject.setVisible(true);
                    notificationField.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
                    notificationField.setText("User: " + buyNowEntry.getBuyerUserName() + ", want to buy [ " + buyNowEntry.getLotIndex() + " ] for £" + buyNowEntry.getLotIndex());

                    btn_reject.addEventHandler(ActionEvent.ACTION, e -> {
                        System.out.println("Pressed button reject");
                        rejectButtonAction(buyNowEntry.getIndex());
                        System.out.println("After btn_reject action");
                        notificationField.setVisible(false);
                        btn_accept.setVisible(false);
                        btn_reject.setVisible(false);
                    });
                } else {
                    System.out.println("notify: not for me");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void acceptButtonAction(ActionEvent event) {
        System.out.println("Accept");
    }

    public void rejectButtonAction(Integer index){

        System.out.println("Reject");

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 3000);
            } catch (Exception e) {
                System.out.println("REject Button Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                BuyNowEntry buyNowEntryTemplate = new BuyNowEntry(index);
                BuyNowEntry buyNowEntry = (BuyNowEntry) space.take(buyNowEntryTemplate, txn, 2000);

                if (buyNowEntry == null) {
                    System.out.println("REject Button Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                } else {
                    System.out.println("REject Button Read the initial buyNowEntry " + buyNowEntry.toString());
                }

                // ... edit that object and write it back again...
                buyNowEntry.setAccepted(false);
                buyNowEntry.setResponse(true);
                space.write(buyNowEntry, txn, Lease.FOREVER);
                System.out.println("REject Button Changed the buyNowEntry '" + buyNowEntry.toString() + "' and written it back to the space");

            } catch (Exception e) {
                System.out.println("REject Button Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }
            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }

    }
}
