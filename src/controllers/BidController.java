package controllers;

import entries.BidU1264982;
import entries.BidIndexU1264982;
import entries.LotU1264982;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import models.Bid;
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

public class BidController implements RemoteEventListener {

    private JavaSpace05 space;
    private TextField notificationField;

    public BidController(JavaSpace05 space, TextField notificationField) {
        this.space = space;
        this.notificationField = notificationField;

        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        try {
            RemoteEventListener remoteEventListener = (RemoteEventListener) myDefaultExporter.export(this);

            BidU1264982 bidTemplate = new BidU1264982();
            space.notify(bidTemplate, null, remoteEventListener, Lease.FOREVER, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBidIndexToSpace();
    }

    public ObservableList<Bid> loadBidsFromSpace(ObservableList<Bid> bids_data, int lotId ) {
        Collection<BidU1264982> list = new ArrayList<BidU1264982>();

        BidU1264982 template = new BidU1264982();
        template.setLotIndex(lotId);
        list.add(template);
        BidU1264982 bid;

        try {
            MatchSet allBits = space.contents(list, null, 5L * 60000L, Integer.MAX_VALUE);
            while (allBits != null) {
                try {
                    bid = (BidU1264982) allBits.next();
                    if (bid == null)
                        break;
                    else {
                        System.out.println(bid.toString());
                        bids_data.add(new Bid(String.valueOf(bid.getIndex()), String.valueOf(bid.getBidValue()), bid.getUserBuyer()));
                    }
                } catch (UnusableEntryException uee) {
                    uee.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bids_data;
    }

    // Bid selected lot with bid value, userBid and transaction manager
    public void bidLot(TransactionManager transactionManager, int lotIndex, double bidValue, String userBid) {
        try {
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 2000);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }
            Transaction txn = trc.transaction;
            try {
                BidIndexU1264982 bidIndexTemplate = new BidIndexU1264982();
                BidIndexU1264982 bidIndex = (BidIndexU1264982) space.take(bidIndexTemplate, txn, Long.MAX_VALUE);
                if(bidIndex == null){
                    System.err.println("Bid Index not found.");
                } else {
                    int index = bidIndex.getIndex();
                    BidU1264982 newBid = new BidU1264982(index, lotIndex, bidValue, userBid);
                    space.write(newBid, null, Lease.FOREVER);
                    bidIndex.increment();
                    space.write(bidIndex, txn, Lease.FOREVER);
                }
            }  catch ( Exception e) {
                e.printStackTrace();
            }
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        BidIndexU1264982 bidIndexTemplate = new BidIndexU1264982();
        BidU1264982 bidTemplate = new BidU1264982();

        try {
            BidIndexU1264982 bidIndex = (BidIndexU1264982) space.readIfExists(bidIndexTemplate, null, 1000);
            if(bidIndex == null) {
                System.out.println("BidIndex object no found in space");
            } else {
                bidTemplate.setIndex(bidIndex.getIndex() - 1);

                BidU1264982 bidResult = (BidU1264982) space.readIfExists(bidTemplate, null, 1000);

                if(bidResult == null) {
                    System.out.println("Bid result not found");
                } else {
                    LotU1264982 lotTemplate = new LotU1264982(bidResult.getLotIndex());
                    LotU1264982 lot = (LotU1264982) space.readIfExists(lotTemplate, null, 1000);

                    if(lot == null) {
                        System.out.println("Lot not found");
                    } else {
                        notificationField.clear();
                        notificationField.setStyle("-fx-text-fill: #1620A1; -fx-font-size: 14px;");
                        notificationField.setText("User: [ " + bidResult.getUserBuyer() + " ], bid lot: [ " + lot.getTitle() + " ], price: £" + bidResult.getBidValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takeSelectedLotBidsFromSpace(int lotIndex) {
        BidU1264982 bidTemplate = new BidU1264982(lotIndex);

        boolean somethingToTake = true;

        // Starting to remove all the Bids belongs to Lot from the space
        while(somethingToTake) {
            try {
                BidU1264982 bidEntry = (BidU1264982) space.takeIfExists(bidTemplate, null, 0);
                if(bidEntry == null) {
                    somethingToTake = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addBidIndexToSpace() {
        BidIndexU1264982 bidIndexTemplate = new BidIndexU1264982();
        try {
            BidIndexU1264982 bidIndex = (BidIndexU1264982)space.readIfExists(bidIndexTemplate,null, Long.MAX_VALUE);
            if (bidIndex == null) {
                try {
                    BidIndexU1264982 bid = new BidIndexU1264982(0);
                    space.write(bid, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
