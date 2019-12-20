package entries;

import net.jini.core.entry.Entry;

public class UserU1264982 implements Entry {

    public Integer index;
    public String username;
    public String password;
    public Boolean isSingIn;
    public Double balance;

    public UserU1264982() {
        // no arg constructor
    }

    public UserU1264982(String username) {
        this.username = username;
    }

    public UserU1264982(Integer index, String username, String password, Boolean isSingIn, Double balance) {
        this.index = index;
        this.username = username;
        this.password = password;
        this.isSingIn = isSingIn;
        this.balance = balance;
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "UserEntry{" +
                "index=" + index +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isSingIn=" + isSingIn +
                ", balance=" + balance +
                '}';
    }
}
