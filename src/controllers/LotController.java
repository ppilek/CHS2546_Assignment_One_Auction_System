package controllers;

import entries.LotEntry;
import entries.LotIndexEntry;
import javafx.collections.ObservableList;
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
    private RemoteEventListener theStub;
    private ObservableList<Lot> lots_data;

    public LotController(JavaSpace05 space, ObservableList<Lot> lots_data) {

        this.space = space;
        this.lots_data = lots_data;

        addLotIndexToSpace();

        // create the exporter
        Exporter myDefaultExporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stub'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            LotEntry template = new LotEntry();
            space.notify(template, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Add Lot to Space with transaction manager
    public void addLotToSpace(TransactionManager transactionManager, String userSeller, String title, String description, double price) {

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
                LotIndexEntry lotIndexTemplate = new LotIndexEntry();
                LotIndexEntry lotIndex = (LotIndexEntry) space.take(lotIndexTemplate, txn, 1000);
//            System.out.println("Lot Index before: " + lotIndex.toString());

                if (lotIndex == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                    System.exit(1);
                } else {
                    System.out.println("Read the initial Lot: " + lotIndex.toString());
                }

                // ... edit that object and write it back again...
                int index = lotIndex.index;

                LotEntry newLot = new LotEntry(index, userSeller, title, description, price);
                space.write(newLot, null, Lease.FOREVER);
                System.out.println("New lot was added to space: " + newLot.toString());

                // update the QueueStatus object by incrementing the counter and write it back to the space
                lotIndex.increment();
                space.write(lotIndex, txn, Lease.FOREVER);
                System.out.println("Lot Index after: " + lotIndex.toString());

            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
                System.exit(1);
            }
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }

    public ObservableList<Lot> loadLotsFromSpace(ObservableList<Lot> lots_data ) {
        Collection<LotEntry> templateList = new ArrayList<LotEntry>();
        LotEntry template = new LotEntry();
        templateList.add(template);
        LotEntry lotEntry = null;

        try {
            MatchSet allLotsFromSpace = space.contents(templateList, null, 5L * 60000L, Integer.MAX_VALUE);
            while (allLotsFromSpace != null) {
                try {
                    lotEntry = (LotEntry) allLotsFromSpace.next();
                    if (lotEntry == null)
                        break;
                    else {
                        System.out.println(lotEntry.toString());
                        lots_data.add(new Lot(String.valueOf(lotEntry.getIndex()), lotEntry.getUserSeller(), lotEntry.getTitle(), lotEntry.getDescription(), String.valueOf(lotEntry.getPrice()), "null"));
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

        LotIndexEntry lotIndexEntryTemplate = new LotIndexEntry();
        LotEntry lotEntryTemplate = new LotEntry();

        try {

            LotIndexEntry lotIndex = (LotIndexEntry) space.readIfExists(lotIndexEntryTemplate, null, 1000);
            lotEntryTemplate.setIndex(lotIndex.getIndex() - 1);

            LotEntry result = (LotEntry) space.readIfExists(lotEntryTemplate, null, 1000);

            lots_data.add(new Lot(String.valueOf(result.getIndex()), result.getUserSeller(), result.getTitle(), result.getDescription(), String.valueOf(result.getPrice()), "null"));

            System.out.println("Notify: " + result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLotIndexToSpace() {
        LotIndexEntry lotIndexTemplate = new LotIndexEntry();
        try {
            LotIndexEntry lotIndex = (LotIndexEntry)space.readIfExists(lotIndexTemplate,null, Long.MAX_VALUE);
            if (lotIndex == null) {
                try {
                    LotIndexEntry lot = new LotIndexEntry(0);
                    space.write(lot, null, Lease.FOREVER);
                    System.out.println("First lot index was added to space.\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Lot index is in the space.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}