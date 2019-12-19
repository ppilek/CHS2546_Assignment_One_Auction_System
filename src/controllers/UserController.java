package controllers;

import entries.UserEntry;
import entries.UserIndexEntry;
import javaspace.SpaceUtils;
import models.User;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace05;

public class UserController {

    private static int ONE_SECOND = 1000;
    private static int TWO_SECONDS = 2000;

    private JavaSpace05 space;
    private TransactionManager transactionManager;

    public UserController(JavaSpace05 space) {
        this.space = space;
        transactionManager = SpaceUtils.getManager();
        addUserIndexToSpace();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        if (transactionManager == null) {
            System.err.println("Failed to find the transaction manager");
        }
    }

    public String singUpToSpace(String username, String password) {
        String status = "";

        try {
            UserEntry userEntryTemplate = new UserEntry(username);
            UserEntry user = (UserEntry) space.read(userEntryTemplate, null, Long.MAX_VALUE);

            if(user == null) {
                status = "User not found";

                try {
                    Transaction.Created trc = null;
                    try {
                        trc = TransactionFactory.create(transactionManager, TWO_SECONDS);
                    } catch (Exception e) {
                        System.out.println("Could not create transaction " + e);
                    }

                    Transaction txn = trc.transaction;

                    try {
                        UserIndexEntry userIndexTemplate = new UserIndexEntry();
                        UserIndexEntry userIndex = (UserIndexEntry) space.take(userIndexTemplate, txn, ONE_SECOND);

                        if (userIndex == null){
                            status = "User Index not found.";
                            txn.abort();
                        } else {
                            System.out.println("User Index before: " + userIndex.toString());

                            int index = userIndex.getIndex();

                            UserEntry newUser = new UserEntry(index, username, password, false, 0.0);
                            space.write(newUser, null, Lease.FOREVER);
                            System.out.println("New User was added to space: " + newUser.toString());

                            userIndex.increment();
                            space.write(userIndex, txn, Lease.FOREVER);
                            System.out.println("User Index after: " + userIndex.toString());
                            status = "Success";
                        }
                    }  catch ( Exception e) {
                        e.printStackTrace();
                        txn.abort();
                    }
                    txn.commit();
                } catch (Exception e) {
                    System.out.print("Transaction failed " + e);
                }
            } else {
                status = "The username already exists. Please use a different username";
            }
        }  catch ( Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public String singInToSpace(String username, String password) {
        String status = "";

        try {

            Transaction.Created trc = null;

            try {
                trc = TransactionFactory.create(transactionManager, TWO_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            try {
                UserEntry userEntryTemplate = new UserEntry(username);
                UserEntry userEntry = (UserEntry) space.read(userEntryTemplate, null, ONE_SECOND);

                if (userEntry == null){
                    status = "Wrong username - please write correct username.";
                    txn.abort();
                } else {

                    if(userEntry.getPassword().equals(password)) {

                        UserEntry user = (UserEntry) space.take(userEntryTemplate, txn, ONE_SECOND);
                        user.setSingIn(true);
                        space.write(user, txn, Lease.FOREVER);
                        System.out.println(user.toString());

                        status = "Success";
                    } else {
                        status = "Wrong password - please write correct password.";
                    }
                }
            }  catch ( Exception e) {
                e.printStackTrace();
                txn.abort();
            }

            txn.commit();

        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }

        return status;
    }

    public String singOutToSpace(String username) {
        String status = "";

        try {

            Transaction.Created trc = null;

            try {
                trc = TransactionFactory.create(transactionManager, TWO_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            Transaction txn = trc.transaction;

            try {
                UserEntry userEntryTemplate = new UserEntry(username);
                UserEntry userEntry = (UserEntry) space.take(userEntryTemplate, txn, ONE_SECOND);

                if (userEntry == null){
                    status = "Wrong username - please write correct username.";
                    txn.abort();
                } else {
                    userEntry.setSingIn(false);
                    space.write(userEntry, txn, Lease.FOREVER);
                    System.out.println(userEntry.toString());
                    status = "Success";
                }
            }  catch ( Exception e) {
                e.printStackTrace();
                txn.abort();
            }

            // ... and commit the transaction.
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }

        return status;
    }

    private void addUserIndexToSpace() {
        UserIndexEntry userIndexTemplate = new UserIndexEntry();
        try {
            UserIndexEntry userIndexEntry = (UserIndexEntry)space.read(userIndexTemplate,null, Long.MAX_VALUE);
            if (userIndexEntry == null) {
                try {
                    UserIndexEntry user = new UserIndexEntry(0);
                    space.write(user, null, Lease.FOREVER);
                    System.out.println("First user index was added to space.\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("User index is in the space.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User readUserInfoFromSpace(String username) {
        User user = new User();
        try {
            UserEntry userEntryTemplate = new UserEntry(username);
            UserEntry userEntry = (UserEntry) space.read(userEntryTemplate, null, Long.MAX_VALUE);

            user.setIndex(userEntry.getIndex());
            user.setUsername(userEntry.getUsername());
            user.setPassword(userEntry.getPassword());
            user.setSingIn(userEntry.getSingIn());
            user.setBalance(userEntry.getBalance());

        }  catch ( Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public void addSoldLotPriceToAccount(String username, double soldPrice) {
        try {
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(transactionManager, TWO_SECONDS);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }
            Transaction txn = trc.transaction;
            try {
                UserEntry userEntryTemplate = new UserEntry(username);
                UserEntry userEntry = (UserEntry) space.take(userEntryTemplate, txn, ONE_SECOND);
                if (userEntry == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                } else {
                    System.out.println("Read the initial User: " + userEntry.toString());
                    userEntry.setBalance(userEntry.getBalance() + soldPrice);
                    space.write(userEntry, txn, Lease.FOREVER);
                    System.out.println("Changed the User balance '" + userEntry.toString() + "' and written it back to the space");
                }
            } catch (Exception e) {
                System.out.println("Failed to read or write to space " + e);
                txn.abort();
            }
            txn.commit();
        } catch (Exception e) {
            System.out.print("Transaction failed " + e);
        }
    }
}
