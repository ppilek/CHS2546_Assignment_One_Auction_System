package controllers;

import entries.UserU1264982;
import entries.UserIndexU1264982;
import javafx.scene.control.Alert;
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

    public UserController() {
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

        addUserIndexToSpace();
    }

    public JavaSpace05 getSpace() {
        return space;
    }

    public String singUpToSpace(String username, String password) {
        String status = "";
        try {
            UserU1264982 userTemplate = new UserU1264982(username);
            UserU1264982 user = (UserU1264982) space.readIfExists(userTemplate, null, TWO_SECONDS);
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
                        UserIndexU1264982 userIndexTemplate = new UserIndexU1264982();
                        UserIndexU1264982 userIndex = (UserIndexU1264982) space.take(userIndexTemplate, txn, ONE_SECOND);
                        if (userIndex == null){
                            status = "User Index not found.";
                            txn.abort();
                        } else {
                            int index = userIndex.getIndex();
                            UserU1264982 newUser = new UserU1264982(index, username, password, false, 0.0);
                            space.write(newUser, null, Lease.FOREVER);
                            userIndex.increment();
                            space.write(userIndex, txn, Lease.FOREVER);
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
                UserU1264982 userTemplate = new UserU1264982(username);
                UserU1264982 readUser = (UserU1264982) space.readIfExists(userTemplate, null, ONE_SECOND);
                if (readUser == null){
                    status = "Wrong username - please write correct username.";
                    txn.abort();
                } else {
                    if(readUser.getPassword().equals(password)) {
                        UserU1264982 user = (UserU1264982) space.take(userTemplate, txn, ONE_SECOND);
                        user.setSingIn(true);
                        space.write(user, txn, Lease.FOREVER);
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
                UserU1264982 userTemplate = new UserU1264982(username);
                UserU1264982 user = (UserU1264982) space.take(userTemplate, txn, ONE_SECOND);
                if (user == null){
                    status = "Wrong username - please write correct username.";
                    txn.abort();
                } else {
                    user.setSingIn(false);
                    space.write(user, txn, Lease.FOREVER);
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
        return status;
    }

    private void addUserIndexToSpace() {
        UserIndexU1264982 userIndexTemplate = new UserIndexU1264982();
        try {
            UserIndexU1264982 userIndexEntry = (UserIndexU1264982)space.readIfExists(userIndexTemplate,null, Long.MAX_VALUE);
            if (userIndexEntry == null) {
                try {
                    UserIndexU1264982 user = new UserIndexU1264982(0);
                    space.write(user, null, Lease.FOREVER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User readUserInfoFromSpace(String username) {
        User user = new User();
        try {
            UserU1264982 userTemplate = new UserU1264982(username);
            UserU1264982 readUser = (UserU1264982) space.read(userTemplate, null, Long.MAX_VALUE);

            user.setIndex(readUser.getIndex());
            user.setUsername(readUser.getUsername());
            user.setPassword(readUser.getPassword());
            user.setSingIn(readUser.getSingIn());
            user.setBalance(readUser.getBalance());
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
                UserU1264982 userEntryTemplate = new UserU1264982(username);
                UserU1264982 userEntry = (UserU1264982) space.take(userEntryTemplate, txn, ONE_SECOND);
                if (userEntry == null) {
                    System.out.println("Error - No object found in space");
                    txn.abort();
                } else {
                    userEntry.setBalance(userEntry.getBalance() + soldPrice);
                    space.write(userEntry, txn, Lease.FOREVER);
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