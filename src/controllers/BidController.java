package controllers;

import entries.BidEntry;
import entries.BidIndexEntry;
import javafx.collections.ObservableList;
import models.Bid;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;
import java.util.ArrayList;
import java.util.Collection;

public class BidController {

    private JavaSpace05 space;

    public BidController(JavaSpace05 space) {
        this.space = space;
        addBidIndexToSpace();
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
}
