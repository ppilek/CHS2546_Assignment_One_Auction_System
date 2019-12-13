package entries;

import net.jini.core.entry.Entry;

public class UserEntry implements Entry {

    public Integer index;
    public String username;
    public String password;
    public Boolean isSingIn;

    public UserEntry() {
        // no arg constructor
    }

    public UserEntry(String username) {
        this.username = username;
    }

    public UserEntry(Integer index, String username, String password, Boolean isSingIn) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.isSingIn = isSingIn;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSingIn() {
        return isSingIn;
    }

    public void setSingIn(Boolean singIn) {
        isSingIn = singIn;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "index=" + index +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isSingIn=" + isSingIn +
                '}';
    }
}