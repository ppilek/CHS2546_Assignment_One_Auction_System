package controllers;

import entries.UserEntry;
import entries.UserIndexEntry;
import models.User;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace05;

public class UserController {

    private JavaSpace05 space;

    public UserController(JavaSpace05 space) {
        this.space = space;
        addUserIndexToSpace();
    }

    public String singUpToSpace(String username, String password) {
        String status = "";

        try {
            UserEntry userEntryTemplate = new UserEntry(username);
            UserEntry user = (UserEntry) space.readIfExists(userEntryTemplate, null, Long.MAX_VALUE);

            if(user == null) {
                status = "User not found";
                try {
                    UserIndexEntry userIndexTemplate = new UserIndexEntry();
                    UserIndexEntry userIndex = (UserIndexEntry) space.take(userIndexTemplate, null, Long.MAX_VALUE);
                    System.out.println("User Index before: " + userIndex.toString());

                    // if there is no LotIndex object in the space then we can't do much, so print an error and exit
                    if (userIndex == null){
                        status = "User Index not found.";
                    }

                    int index = userIndex.index;

                    UserEntry newUser = new UserEntry(index, username, password, false);
                    space.write(newUser, null, Lease.FOREVER);
                    System.out.println("New User was added to space: " + newUser.toString());

                    // update the QueueStatus object by incrementing the counter and write it back to the space
                    userIndex.increment();
                    space.write(userIndex, null, Lease.FOREVER);
                    System.out.println("User Index after: " + userIndex.toString());
                    status = "Success";

                }  catch ( Exception e) {
                    e.printStackTrace();
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
            UserEntry userEntryTemplate = new UserEntry(username);
            UserEntry userEntry = (UserEntry) space.takeIfExists(userEntryTemplate, null, Long.MAX_VALUE);

            if (userEntry == null){
                status = "Wrong username - please write correct username.";
            } else {
                if(userEntry.getPassword().equals(password)) {

                    userEntry.setSingIn(true);
                    space.write(userEntry, null, Lease.FOREVER);
                    System.out.println(userEntry.toString());

                    status = "Success";
                } else {
                    status = "Wrong password - please write correct password.";
                }
            }

        }  catch ( Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public String singOutToSpace(String username) {
        String status = "";
        try {
            UserEntry userEntryTemplate = new UserEntry(username);
            UserEntry userEntry = (UserEntry) space.takeIfExists(userEntryTemplate, null, Long.MAX_VALUE);

            if (userEntry == null){
                status = "Wrong username - please write correct username.";
            } else {
                userEntry.setSingIn(false);
                space.write(userEntry, null, Lease.FOREVER);
                System.out.println(userEntry.toString());
                status = "Success";
            }

        }  catch ( Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    private void addUserIndexToSpace() {
        UserIndexEntry userIndexTemplate = new UserIndexEntry();
        try {
            UserIndexEntry bidIndex = (UserIndexEntry)space.readIfExists(userIndexTemplate,null, Long.MAX_VALUE);
            if (bidIndex == null) {
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
            UserEntry userEntry = (UserEntry) space.readIfExists(userEntryTemplate, null, Long.MAX_VALUE);

            user.setIndex(userEntry.getIndex());
            user.setUsername(userEntry.getUsername());
            user.setPassword(userEntry.getPassword());
            user.setSingIn(userEntry.getSingIn());

        }  catch ( Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}
