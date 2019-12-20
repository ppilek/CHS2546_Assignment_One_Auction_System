package controllers;

import entries.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import models.Bid;
import models.Lot;
import net.jini.core.entry.UnusableEntryException;
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
import net.jini.space.MatchSet;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

public class LotController implements RemoteEventListener {

    private JavaSpace05 space;
    private ObservableList<Lot> lots_data;
    private TableView<Lot> lots_table_data;

    public LotController(JavaSpace05 space, ObservableList<Lot> lots_data, TableView<Lot> lots_table_data) {

        this.space = space;
        this.lots_data = lots_data;
        this.lots_table_data = lots_table_data;

        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        try {
            RemoteEventListener remoteEventListener = (RemoteEventListener) myDefaultExporter.export(this);
            LotU1264982 lotTemplate = new LotU1264982();
            space.notify(lotTemplate, null, remoteEventListener, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addLotIndexToSpace();
    }

    // Add Lot to Space with transaction manager
    public void addLot(TransactionManager transactionManager, String userSeller, String title, String description, double originalPrice) {

        try {
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 2000);
            } catch (Exception e) {
                System.err.println("Could not create transaction " + e);
            }
            Transaction txn = trc.transaction;
            try {
                LotIndexU1264982 lotIndexTemplate = new LotIndexU1264982();
                LotIndexU1264982 lotIndex = (LotIndexU1264982) space.takeIfExists(lotIndexTemplate, txn, 1000);

                if (lotIndex == null) {
                    System.err.println("Error - No object found in space");
                    txn.abort();
                } else {
                    int index = lotIndex.index;
                    LotU1264982 newLot = new LotU1264982(index, userSeller, title, description, originalPrice, false, 0.0, "pending", "null");
                    space.write(newLot, null, Lease.FOREVER);
                    lotIndex.increment();
                    space.write(lotIndex, txn, Lease.FOREVER);
                }
            } catch (Exception e) {
                System.err.println("Failed to read or write to space " + e);
                txn.abort();
            }
            txn.commit();
        } catch (Exception e) {
            System.err.print("Transaction failed " + e);
        }
    }

    // Update Lot from space and send it back with transaction manager
    public void updateLot(TransactionManager transactionManager, int lotIndex, String userBuyer, double soldPrice) {
        try {
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 2000);
            } catch (Exception e) {
                System.err.println("Could not create transaction " + e);
            }
            Transaction txn = trc.transaction;
            try {
                LotU1264982 lotTemplate = new LotU1264982(lotIndex);
                LotU1264982 lot = (LotU1264982) space.takeIfExists(lotTemplate, txn, 1000);
                if (lot == null){
                    System.err.println("Lot not found.");
                } else {
                    lot.setSoldPrice(soldPrice);
                    lot.setStatus("sold");
                    lot.setUserBuyer(userBuyer);
                    space.write(lot, txn, Lease.FOREVER);
                    addNotificationToSpace(lot.getIndex(), lot.getUserSeller(), lot.getTitle(), lot.getDescription(), lot.getSoldPrice(), false, userBuyer);
                }
            } catch (Exception e) {
                System.err.println("Failed to read or write to space " + e);
                txn.abort();
            }
            txn.commit();
        } catch (Exception e) {
            System.err.print("Transaction failed " + e);
        }
    }

    // sold lot by accept selected bid
    public void acceptSelectedBid(TransactionManager transactionManager,Lot selectedLot, Bid selectedBid) {
        try {
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 2000);
            } catch (Exception e) {
                System.err.println("Could not create transaction " + e);
            }
            Transaction txn = trc.transaction;
            try {
                int selectedLotIndex = Integer.parseInt(selectedLot.getIndex());
                LotU1264982 lotTemplate = new LotU1264982(selectedLotIndex);
                LotU1264982 lot = (LotU1264982) space.takeIfExists(lotTemplate, txn, 1000);

                if (lot == null){
                    System.err.println("Lot not found.");
                } else {
                    lot.setSoldPrice(Double.valueOf(selectedBid.getBidValue()));
                    lot.setStatus("sold");
                    lot.setUserBuyer(selectedBid.getUser());
                    space.write(lot, txn, Lease.FOREVER);
                    addNotificationToSpace(lot.getIndex(), lot.getUserSeller(), lot.getTitle(), lot.getDescription(), lot.getSoldPrice(), true, selectedBid.getUser());
                }
            } catch (Exception e) {
                System.err.println("Failed to read or write to space " + e);
                txn.abort();
            }
            txn.commit();
        } catch (Exception e) {
            System.err.println("Transaction failed " + e);
        }
    }

    public ObservableList<Lot> loadLotsFromSpace(ObservableList<Lot> lots_data ) {
        Collection<LotU1264982> templateList = new ArrayList<LotU1264982>();
        LotU1264982 template = new LotU1264982();
        templateList.add(template);
        LotU1264982 lotEntry;
        try {
            MatchSet allLotsFromSpace = space.contents(templateList, null, 5L * 60000L, Integer.MAX_VALUE);
            while (allLotsFromSpace != null) {
                try {
                    lotEntry = (LotU1264982) allLotsFromSpace.next();
                    if (lotEntry == null)
                        break;
                    else {
                        lots_data.add(new Lot(String.valueOf(lotEntry.getIndex()), lotEntry.getUserSeller(), lotEntry.getTitle(), lotEntry.getDescription(), String.valueOf(lotEntry.getOriginalPrice()), String.valueOf(lotEntry.getSoldPrice()), lotEntry.getStatus(), lotEntry.getUserBuyer()));
                    }
                } catch (UnusableEntryException uee) {
                    uee.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lots_data;
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        lots_table_data.getItems().clear();
        lots_table_data.setItems(loadLotsFromSpace(lots_data));
    }

    public void removeSelectedLotFromSpace(int lotIndex) {
        LotU1264982 lotTemplate = new LotU1264982(lotIndex);
        try {
            space.takeIfExists(lotTemplate, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLotIndexToSpace() {
        LotIndexU1264982 lotIndexTemplate = new LotIndexU1264982();
        try {
            LotIndexU1264982 lotIndex = (LotIndexU1264982)space.readIfExists(lotIndexTemplate,null, Long.MAX_VALUE);
            if (lotIndex == null) {
                try {
                    LotIndexU1264982 lot = new LotIndexU1264982(0);
                    space.write(lot, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addNotificationToSpace(int lotIndex, String lotSeller, String lotTitle, String lotDescription, Double lotPrice, boolean isBid, String lotBuyer) {
        try{
            NotificationU1264982 notificationEntry = new NotificationU1264982(lotIndex, lotSeller, lotTitle, lotDescription, lotPrice, isBid, lotBuyer);
            space.write(notificationEntry, null, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}