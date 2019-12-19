package controllers;

import entries.BidEntry;
import entries.BidIndexEntry;
import entries.LotEntry;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import models.Bid;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
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
    private RemoteEventListener theStub;
    private TextField notificationField;

    public BidController(JavaSpace05 space, TextField notificationField) {
        this.space = space;
        this.notificationField = notificationField;
        addBidIndexToSpace();

        // create the exporter
        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            BidEntry bidEntryTemplate = new BidEntry();
            space.notify(bidEntryTemplate, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Bid> loadBidsFromSpace(ObservableList<Bid> bids_data, int lotId ) {
        Collection<BidEntry> list = new ArrayList<BidEntry>();

        BidEntry template = new BidEntry();
        template.setLotIndex(lotId);
        list.add(template);
        BidEntry bidEntry = null;

        try {
            MatchSet allBitsForLotId = space.contents(list, null, 5L * 60000L, Integer.MAX_VALUE);
            while (allBitsForLotId != null) {
                try {
                    bidEntry = (BidEntry) allBitsForLotId.next();
                    if (bidEntry == null)
                        break;
                    else {
                        System.out.println(bidEntry.toString());
                        bids_data.add(new Bid(String.valueOf(bidEntry.getIndex()), String.valueOf(bidEntry.getBidValue()), bidEntry.getUserBuyer()));
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

    public void bidLot(int lotIndex, double bidValue, String userBuyer) {
        try {
            BidIndexEntry bidIndexTemplate = new BidIndexEntry();
            BidIndexEntry bidIndex = (BidIndexEntry) space.take(bidIndexTemplate, null, Long.MAX_VALUE);
            System.out.println("Bid Index before: " + bidIndex.toString());

            if(bidIndex == null){
                System.out.println("Bid Index not found.");
                System.exit(1);
            }

            int index = bidIndex.getIndex();

            BidEntry newBid = new BidEntry(index, lotIndex, bidValue, userBuyer);
            space.write(newBid, null, Lease.FOREVER);
            System.out.println("New Bid was added to space: " + newBid.toString());

            bidIndex.increment();
            space.write(bidIndex, null, Lease.FOREVER);
            System.out.println("Bid Index after: " + bidIndex.toString());

        }  catch ( Exception e) {
            e.printStackTrace();
        }

    }

    private void addBidIndexToSpace() {
        BidIndexEntry bidIndexTemplate = new BidIndexEntry();
        try {
            BidIndexEntry bidIndex = (BidIndexEntry)space.readIfExists(bidIndexTemplate,null, Long.MAX_VALUE);
            if (bidIndex == null) {
                try {
                    BidIndexEntry bid = new BidIndexEntry(0);
                    space.write(bid, null, Lease.FOREVER);
                    System.out.println("First bid index was added to space.\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Bid index is in the space.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(RemoteEvent remoteEvent) throws UnknownEventException, RemoteException {
        BidIndexEntry bidIndexEntryTemplate = new BidIndexEntry();
        BidEntry bidEntryTemplate = new BidEntry();

        try {
            BidIndexEntry bidIndex = (BidIndexEntry) space.readIfExists(bidIndexEntryTemplate, null, 1000);
            if(bidIndex == null) {
                System.out.println("BidIndexEntry object no found in space.");
            } else {
                bidEntryTemplate.setIndex(bidIndex.getIndex() - 1);

                BidEntry bidEntryResult = (BidEntry) space.readIfExists(bidEntryTemplate, null, 1000);

                if(bidEntryResult == null) {
                    System.out.println("Lot not found wit bid entry");
                } else {
                    LotEntry lotEntryTemplate = new LotEntry(bidEntryResult.getLotIndex());

                    LotEntry lotEntry = (LotEntry) space.readIfExists(lotEntryTemplate, null, 1000);

                    if(lotEntry == null) {
                        System.out.println("Lot not found wit bid entry");
                    } else {
                        System.out.println("Notification from Bid: " + bidEntryResult.toString());
                        System.out.println("Notification from Lot:" + lotEntry.toString());

                        notificationField.clear();
                        notificationField.setStyle("-fx-text-fill: #1620A1; -fx-font-size: 14px;");
                        notificationField.setText("User: [ " + bidEntryResult.getUserBuyer() + " ], bid lot: [ " + lotEntry.getTitle() + " ], price: £" + bidEntryResult.getBidValue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takeSelectedLotBidsFromSpace(int lotIndex) {
        BidEntry bidEntryTemplate = new BidEntry(lotIndex);
        // Take all lots that match our template back out of the space
        // This is just to tidy up, really.
        int bidsTaken = 0;
        boolean somethingToTake = true;

        System.out.println("\n\nStarting to remove all the Bids for Lot from the space...\n");

        while(somethingToTake) {
            try {
                BidEntry bidEntry = (BidEntry) space.takeIfExists(bidEntryTemplate, null, 0);

                if(bidEntry != null) {
                    System.out.println("Removed a Bid with ID " + bidEntry.getIndex());
                    bidsTaken++;
                } else {
                    somethingToTake = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nRemove ended. " + bidsTaken + " bids were removed from the space\n");
    }
}
