package controllers;

import entries.*;
import models.Lot;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;

import java.rmi.RemoteException;

public class BuyNowController {

    private JavaSpace05 space;

    public BuyNowController(JavaSpace05 space) {
        this.space = space;
        addBuyNowIndexToSpace();
    }

    // Done add BuyNow to Space with transaction manager
    public void addBuyNowToSpace(TransactionManager transactionManager, int lotIndex, String userBuyer) {
        System.out.println("addBuyNowToSpace");

        try {
            // First we need to create the transaction object
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, 3000);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            // Now take the initial object back out of the space...
            try {
                LotEntry lotEntryTemplate = new LotEntry(lotIndex);
                LotEntry lot = (LotEntry) space.readIfExists(lotEntryTemplate, null, 1000);

                if (lot == null){
                    System.out.println("Lot not found.");
                } else {
                    System.out.println("Lot founded");
                    BuyNowIndexEntry buyNowIndexEntryTemplate = new BuyNowIndexEntry();
                    BuyNowIndexEntry buyNowIndexEntry = (BuyNowIndexEntry) space.take(buyNowIndexEntryTemplate, txn, 1000);

                    if (buyNowIndexEntry == null) {
                        System.out.println("Error - No object found in space");
                        txn.abort();
                        System.exit(1);
                    } else {
                        System.out.println("buyNowIndexEntry before: " + buyNowIndexEntry.toString());
                        // ... edit that object and write it back again...
                        int index = buyNowIndexEntry.index;

                        BuyNowEntry newBuyNowEntry = new BuyNowEntry(index, false, lot.getUserSeller(), lot.getIndex(), userBuyer, false);
                        space.write(newBuyNowEntry, null, Lease.FOREVER);
                        System.out.println("newBuyNowEntry was added to space: " + newBuyNowEntry.toString());

                        // update the QueueStatus object by incrementing the counter and write it back to the space
                        buyNowIndexEntry.increment();
                        space.write(buyNowIndexEntry, txn, Lease.FOREVER);
                        System.out.println("buyNowIndexEntry after: " + buyNowIndexEntry.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }
            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }

    private void addBuyNowIndexToSpace() {
        BuyNowIndexEntry buyNowIndexEntryTemplate = new BuyNowIndexEntry();
        try {
            BuyNowIndexEntry buyNowIndexEntry = (BuyNowIndexEntry)space.readIfExists(buyNowIndexEntryTemplate,null, Long.MAX_VALUE);
            if (buyNowIndexEntry == null) {
                try {
                    BuyNowIndexEntry buyNow = new BuyNowIndexEntry(0);
                    space.write(buyNow, null, Lease.FOREVER);
                    System.out.println("First BuyNowIndex was added to space.\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("BuyNowIndex is in the space.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
